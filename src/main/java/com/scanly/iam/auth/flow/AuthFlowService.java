package com.scanly.iam.auth.flow;

import com.scanly.common.hashing.HashingService;
import com.scanly.iam.auth.common.dto.request.StepAuthRequest;
import com.scanly.iam.auth.common.dto.response.AuthResponse;
import com.scanly.iam.auth.common.dto.response.ChallengeAuthResponse;
import com.scanly.iam.auth.common.dto.response.FinalAuthResponse;
import com.scanly.iam.auth.common.dto.response.StepAuthResponse;
import com.scanly.iam.auth.common.exception.*;
import com.scanly.iam.auth.common.model.AuthAction;
import com.scanly.iam.auth.common.model.AuthMethodType;
import com.scanly.iam.auth.common.model.StepContext;
import com.scanly.iam.auth.common.service.AuthHandler;
import com.scanly.iam.auth.flow.model.AuthFlowState;
import com.scanly.iam.auth.flow.model.AuthStep;
import com.scanly.iam.auth.flow.model.AuthStepStatus;
import com.scanly.iam.token.AuthToken;
import com.scanly.iam.token.AuthTokenIssuer;
import com.scanly.iam.user.domain.User;
import com.scanly.iam.user.domain.UserStatus;
import com.scanly.iam.user.service.UserLookupService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Core service orchestrating the state transitions for authentication flow.
 * <p>
 * The {@code AuthFlowService} acts as the primary state machine for user logins.
 * It manages the transition from identification to multifactor verification
 * and final token issuance.
 * </p>
 *
 * <p>
 * It relies on Redis for stateless session management and ensures that all security
 * requirements are satisfied in the correct sequence.
 * </p>
 *
 * <p>
 *     {@link AuthHandler}, the central orchestrator for the authentication process,
 *     relies on {@link AuthFlowService} for the {@code Authorize} and {@code Advance}
 *     steps.
 * </p>
 */
@Service
public class AuthFlowService {
    /** Duration for which a login flow remains active */
    private static final Duration AUTH_FLOW_TTL = Duration.ofMinutes(10);

    /** Cache abstraction for persisting and retrieving the state of an active auth flow. */
    private final AuthFlowCache authFlowCache;

    /** Component responsible for determining the required authentication steps */
    private final AuthStepsResolver authStepsResolver;

    /** Domain service for retrieving user information and account status. */
    private final UserLookupService userLookupService;

    /** Service required to hash the mfa tokens to store safely in redis */
    private final HashingService hashingService;

    /** Service required for TokenGeneration for successful login */
    private final AuthTokenIssuer authTokenIssuer;

    public AuthFlowService(
            AuthFlowCache authFlowCache,
            AuthStepsResolver authStepsResolver,
            UserLookupService userLookupService,
            HashingService hashingService,
            AuthTokenIssuer authTokenIssuer
    ) {
        this.authFlowCache = authFlowCache;
        this.authStepsResolver = authStepsResolver;
        this.userLookupService = userLookupService;
        this.hashingService = hashingService;
        this.authTokenIssuer = authTokenIssuer;
    }

    /**
     * Bootstraps a new authentication flow for the given user email.
     * <p>
     * This method resolves the identity, determines the MFA roadmap, and
     * initializes the session state in the cache.
     * </p>
     *
     * @param email The normalized email of the user attempting to log in.
     * @return A {@link StepAuthResponse} containing the generated {@code flowId}
     * and the information about next steps to be performed.
     */
    public AuthResponse initiate(String email) {
        User user = findUserByEmail(email);

        List<AuthStep> requiredSteps = authStepsResolver.resolveSteps(user.userId());

        Instant now = Instant.now();
        AuthFlowState flowState = new AuthFlowState(
                user.userId(),
                now,
                now.plusSeconds(AUTH_FLOW_TTL.toSeconds()),
                requiredSteps,
                new HashMap<>()
        );

        String flowId = UUID.randomUUID().toString();
        authFlowCache.saveFlowState(flowId, flowState);
        return new StepAuthResponse(
                flowId,
                null,
                true,
                extractAuthMethods(requiredSteps.getFirst())
        );
    }

    /**
     * Retrieves the user and validates that the account is currently active.
     *
     * @param email The user's email.
     * @return The {@link User} domain object.
     * @throws UserNotFoundException if no user matches the email.
     * @throws UserNotActiveException if the account is locked or disabled.
     */
    @NonNull
    private User findUserByEmail(String email) {
        User user = userLookupService.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UserNotFoundException(
                    String.format("User with email %s not found", email)
            );
        }

        if (user.status() != UserStatus.ACTIVE) {
            throw new UserNotActiveException(
                    String.format("User with email %s is not active", email)
            );
        }
        return user;
    }

    /**
     * Utility to extract the names of permitted authentication methods for a step.
     */
    private List<String> extractAuthMethods(AuthStep step) {
        return step
                .allowedAuthMethods()
                .stream()
                .map(AuthMethodType::name)
                .toList();
    }

    /**
     * Validates the integrity of an individual authentication step.
     * <p>
     * This method acts as a gatekeeper, checking session existence, account status,
     * step sequencing, MFA token validity, and action-to-state compatibility
     * before allowing an operation to proceed.
     * </p>
     *
     * @param authRequest The incoming step verification or generation request.
     * @return A {@link StepContext} if all validation checks pass.
     */
    public StepContext authorize(StepAuthRequest<?> authRequest) {
        AuthFlowState flowState = getAuthFlowState(authRequest);
        validateUserId(flowState.userId());
        validatePreviousSteps(authRequest, flowState);
        validateMfaToken(authRequest, flowState);
        validateCurrentAuthMethod(authRequest, flowState);
        validateAuthAction(authRequest, flowState);

        return new StepContext(
                flowState.userId(),
                authRequest.flowId()
        );
    }

    /**
     * Retrieves the current flow state from the cache based on the request flowId.
     *
     * @throws AuthSessionExpiredException if the session has expired.
     * @throws InvalidAuthSessionException if the session does not exist.
     */
    @NonNull
    private AuthFlowState getAuthFlowState(StepAuthRequest<?> authRequest) {
        String flowId = authRequest.flowId();
        AuthFlowState flowState = authFlowCache.getFlowState(flowId).orElse(null);

        if (flowState == null) {
            throw new InvalidAuthSessionException(
                    String.format("Flow id %s not found", flowId)
            );
        }

        if (flowState.isExpired()) {
            throw new AuthSessionExpiredException(
                    String.format("Session for Flow id: %s has expired", flowId)
            );
        }
        return flowState;
    }

    /**
     * Verifies that the user associated with the flow is still active.
     */
    private void validateUserId(UUID userId) {
        User user = userLookupService.findById(userId).orElse(null);
        if (user == null) {
            throw new UserNotFoundException(
                    String.format("User with id %s not found", userId)
            );
        }

        if (user.status() != UserStatus.ACTIVE) {
            throw new UserNotActiveException(
                    String.format("User with id %s is not active", userId)
            );
        }
    }

    /**
     * Safely parses the raw authentication method string from the request into an enum.
     */
    private AuthMethodType getAuthMethod(StepAuthRequest<?> authRequest) {
        try {
            return AuthMethodType.valueOf(authRequest.authMethod());
        } catch (IllegalArgumentException e) {
            throw new InvalidAuthMethodException(
                    String.format("Invalid Auth Method: %s", authRequest.authMethod())
            );
        }
    }

    /**
     * Enforces strict sequencing by ensuring all preceding steps are marked as VERIFIED.
     */
    private void validatePreviousSteps(StepAuthRequest<?> authRequest, AuthFlowState flowState) {
        List<AuthStep> requiredSteps = flowState.requiredSteps();
        AuthMethodType currentAuthMethod = getAuthMethod(authRequest);
        for (AuthStep step : requiredSteps) {
            if (step.allowedAuthMethods().contains(currentAuthMethod)) {
                return;
            }
            if (step.verifiedWith() == null) {
                throw new AuthStepSequenceViolationException(
                        String.format(
                                "Invalid Auth Step: %s. Previous steps not completed.",
                                authRequest.authMethod()
                        )
                );
            }
        }
    }

    /**
     * Validates the MFA proof token presented by the client.
     * <p>
     * Ensures that the token matches the secret generated during the successful
     * completion of the immediate previous authentication step.
     * </p>
     */
    private void validateMfaToken(StepAuthRequest<?> authRequest, AuthFlowState flowState) {
        AuthStep previousStep = getPreviousStep(authRequest, flowState);
        if (previousStep == null) {
            return;
        }

        String previousStepVerificationMethod = previousStep.verifiedWith().name();
        String storedMfaTokenHash = flowState.mfaTokens().get(previousStepVerificationMethod);
        String mfaToken = authRequest.mfaToken();

        if (mfaToken == null || !hashingService.verify(storedMfaTokenHash, mfaToken)) {
            throw new InvalidMfaTokenException(
                    String.format("Invalid Mfa Token for the current step %s", authRequest.authMethod())
            );
        }
    }

    /**
     * Finds the previous step of the current step in the flowState.
     */
    private AuthStep getPreviousStep(StepAuthRequest<?> authRequest, AuthFlowState flowState) {
        List<AuthStep> requiredSteps = flowState.requiredSteps();
        AuthMethodType currentAuthMethod = getAuthMethod(authRequest);
        AuthStep previousStep = null;
        for (AuthStep step : requiredSteps) {
            if (step.allowedAuthMethods().contains(currentAuthMethod)) {
                break;
            }
            previousStep = step;
        }
        return previousStep;
    }

    /**
     * Validates that the requested auth method is permitted for the current step
     * and that the step hasn't already been completed.
     */
    private void validateCurrentAuthMethod(StepAuthRequest<?> authRequest, AuthFlowState flowState) {
        AuthMethodType currentAuthMethod = getAuthMethod(authRequest);
        AuthStep currentAuthStep = getAuthStep(flowState, currentAuthMethod);

        if (currentAuthStep == null) {
            throw new AuthMethodNotPermittedException(
                    String.format("Current Auth Method: %s not permitted", authRequest.authMethod())
            );
        }

        if (currentAuthStep.verifiedWith() != null) {
            throw new AuthStepAlreadyCompletedException(
                    String.format("Current step is already completed with Auth Method: %s", currentAuthStep.verifiedWith())
            );
        }
    }

    /**
     * Finds the {@link AuthStep} corresponding to the current auth method.
     */
    private AuthStep getAuthStep(AuthFlowState flowState, AuthMethodType authMethod) {
        return flowState
                .requiredSteps()
                .stream()
                .filter(step -> step.allowedAuthMethods().contains(authMethod))
                .findFirst()
                .orElse(null);
    }

    /**
     * Parses the raw authentication action string from the request.
     */
    private AuthAction getAuthAction(StepAuthRequest<?> authRequest) {
        try {
            return AuthAction.valueOf(authRequest.authAction());
        } catch (IllegalArgumentException e) {
            throw new InvalidAuthActionException(
                    String.format("Invalid Auth Action: %s", authRequest.authAction())
            );
        }
    }

    /**
     * Enforces the protocol of the authentication method.
     * <p>
     * For example, it prevents a 'VERIFY' action if the method requires a
     * 'GENERATE' action first (like SMS) and the step is not in CHALLENGED state.
     * </p>
     */
    private void validateAuthAction(StepAuthRequest<?> authRequest, AuthFlowState flowState) {
        AuthAction authAction = getAuthAction(authRequest);
        AuthMethodType authMethod = getAuthMethod(authRequest);

        if (!authMethod.isActionSupported(authAction)) {
            throw new AuthActionNotSupportedException(
                    String.format(
                            "Auth Action: %s not supported for Auth Method: %s",
                            authRequest.authAction(),
                            authRequest.authMethod()
                    )
            );
        }

        AuthStep currentAuthStep = getAuthStep(flowState, authMethod);

        /*
         * Ensures that the current state of the step is {@link AuthStepStatus#CHALLENGED } for the current
         * auth method if generation is required.
         */
        if (authAction == AuthAction.VERIFY && authMethod.isGenerationRequired()) {
            if (currentAuthStep.status() != AuthStepStatus.CHALLENGED ||
                    currentAuthStep.currentAuthMethod() != authMethod
            ) {
                throw new AuthActionNotPermittedException(
                        String.format("Cannot VERIFY without GENERATE for auth method %s", authRequest.authMethod())
                );
            }
        }
    }

    /**
     * Advances the state of the authentication flow after a successful operation.
     * <p>
     * This method marks steps as verified, generates new proof tokens, and
     * persists the updated session state to the cache.
     * </p>
     *
     * @param authRequest The request that was successfully processed by the executor.
     * @return The next {@link AuthResponse} representing the new state of the machine.
     */
    public AuthResponse advance(StepAuthRequest<?> authRequest) {
        AuthFlowState flowState = getAuthFlowState(authRequest);

        String mfaToken = UUID.randomUUID().toString();
        flowState = updateFlowState(authRequest, flowState, mfaToken);
        AuthResponse authResponse = getAuthResponse(authRequest, flowState, mfaToken);

        if (!authFlowCache.updateFlowState(authRequest.flowId(), flowState)) {
            throw new AuthSessionExpiredException(
                    String.format("Session for Flow Id: %s has expired", authRequest.flowId())
            );
        }
        return authResponse;
    }

    /**
     * Finds the index of the step in the requiredSteps list that contains
     * the requested auth method.
     */
    private int getCurrentStepIndex(StepAuthRequest<?> authRequest, AuthFlowState flowState) {
        AuthMethodType currentAuthMethod = getAuthMethod(authRequest);

        for (int i = 0; i < flowState.requiredSteps().size(); i++) {
            if (flowState.requiredSteps().get(i).allowedAuthMethods().contains(currentAuthMethod)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Performs an immutable update on the flow state based on the completed action.
     * <p>
     * If the action was GENERATE, the step is marked as CHALLENGED.
     * If VERIFY, the step is marked VERIFIED and a new random proof token is issued.
     * </p>
     */
    private AuthFlowState updateFlowState(StepAuthRequest<?> authRequest, AuthFlowState flowState, String mfaToken) {
        AuthMethodType currentAuthMethod = getAuthMethod(authRequest);
        AuthAction currentAuthAction = getAuthAction(authRequest);

        int currentStepIndex = getCurrentStepIndex(authRequest, flowState);
        AuthStep currentStep = flowState.requiredSteps().get(currentStepIndex);

        /* Gets the updated value of current step after performing the given action on current method */
        AuthStep updatedCurrentStep = currentStep.update(currentAuthAction, currentAuthMethod);

        Map<String, String> mfaTokens = new HashMap<>(flowState.mfaTokens());
        if (currentAuthAction == AuthAction.VERIFY) {
            String mfaTokenHash = hashingService.hash(mfaToken);
            mfaTokens.put(currentAuthMethod.name(), mfaTokenHash);
        }

        List<AuthStep> steps = new ArrayList<>(flowState.requiredSteps());
        steps.set(currentStepIndex, updatedCurrentStep);

        return flowState.update(steps, mfaTokens);
    }

    /**
     * Logic factory to determine the appropriate response DTO based on current progress.
     */
    private AuthResponse getAuthResponse(StepAuthRequest<?> authRequest, AuthFlowState flowState, String mfaToken) {
        int currentStepIndex = getCurrentStepIndex(authRequest, flowState);
        AuthStep currentStep = flowState.requiredSteps().get(currentStepIndex);

        if (currentStep.status() == AuthStepStatus.CHALLENGED) {
            return getChallengeAuthResponse();
        } else if (currentStepIndex == (flowState.requiredSteps().size() - 1)) {
            return getFinalAuthResponse(flowState.userId());
        }  else {
            return getStepAuthResponse(authRequest, flowState, mfaToken);
        }
    }

    /**
     * Builds a response indicating the user has been challenged (e.g., OTP sent).
     */
    private AuthResponse getChallengeAuthResponse() {
        return new ChallengeAuthResponse();
    }

    /**
     * Builds a response for moving to the next factor in a multistep sequence.
     * <p>
     * Includes the proof token from the current successful verification.
     * </p>
     */
    private AuthResponse getStepAuthResponse(StepAuthRequest<?> authRequest, AuthFlowState flowState, String mfaToken) {
        int currentStepIndex = getCurrentStepIndex(authRequest, flowState);
        AuthStep nextStep = flowState.requiredSteps().get(currentStepIndex + 1);

        return new StepAuthResponse(
                authRequest.flowId(),
                mfaToken,
                true,
                extractAuthMethods(nextStep)
        );
    }

    /**
     * Builds the final response once all steps are successfully verified.
     * <p>
     *     Coordinates with the {@link AuthTokenIssuer} to get the token
     *     for authenticated user.
     * </p>
     */
    private AuthResponse getFinalAuthResponse(UUID userId) {
        AuthToken authToken = authTokenIssuer.issue(userId);
        return new FinalAuthResponse(
                authToken.accessToken().token(),
                authToken.accessToken().expiresAt()
        );
    }
}