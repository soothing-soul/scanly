package com.scanly.iam.auth.email;

import com.scanly.iam.auth.common.dto.request.StepAuthRequest;
import com.scanly.iam.auth.common.dto.response.AuthResponse;
import com.scanly.iam.auth.common.service.AuthHandler;
import com.scanly.iam.auth.common.service.StepExecutor;
import org.springframework.stereotype.Component;

/**
 * Delegator for Email OTP authentication flows within the IAM domain.
 * <p>
 * This component acts as a domain-specific wrapper around the generic {@link AuthHandler}.
 * It coordinates the lifecycle of email OTP challenges by delegating the actual execution logic
 * to specialized "Executer" components.
 * <p>
 *
 * <p>
 *     As {@link AuthHandler} is typed, this component also serves to maintain the type safety
 *     by passing the related {@code payload} and {@link StepExecutor} to the {@link AuthHandler}.
 *     If not for this, AuthHandler would've maintained a list of StepExecutors of different
 *     types, effectively losing the safety guarantee offered by the generics.
 * </p>
 */
@Component
public class EmailOtpAuthHandler {
    private final AuthHandler authHandler;
    private final EmailOtpGenerationExecuter emailOtpGenerationExecuter;
    private final EmailOtpVerificationExecutor emailOtpVerificationExecutor;

    /**
     * Constructs the handler with its core orchestration engine and specialized executors.
     *
     * @param authHandler                   The generic authentication engine that orchestrates
     *                                      the lifecycle of all the authentication steps.
     * @param emailOtpGenerationExecuter    The strategy for creating and sending an email OTP.
     * @param emailOtpVerificationExecutor  The strategy for validating a received email OTP.
     */
    public EmailOtpAuthHandler(
            AuthHandler authHandler,
            EmailOtpGenerationExecuter emailOtpGenerationExecuter,
            EmailOtpVerificationExecutor emailOtpVerificationExecutor
    ) {
        this.authHandler = authHandler;
        this.emailOtpGenerationExecuter = emailOtpGenerationExecuter;
        this.emailOtpVerificationExecutor = emailOtpVerificationExecutor;
    }

    /**
     * Initiates the "send" phase of the email OTP flow.
     * <p>
     * It passes the generation logic to the {@link AuthHandler}, which ensures the
     * request context is valid before triggering the actual OTP creation and delivery.
     * </p>
     *
     * @param stepAuthRequest The request containing the user's email or identity context.
     * @return An {@link AuthResponse} containing the next steps or status of the dispatch.
     */
    public AuthResponse generate(StepAuthRequest<EmailOtpGenerationPayload> stepAuthRequest) {
        return authHandler.handle(stepAuthRequest, emailOtpGenerationExecuter);
    }

    /**
     * Initiates the "verify" phase of the email OTP flow.
     * <p>
     * This method delegates to the {@link AuthHandler} to process the verification attempt.
     * If successful, the handler will transition the user to the next authentication
     * step or issue the final security tokens.
     * </p>
     *
     * @param stepAuthRequest The request containing the challengeId and the user's OTP input.
     * @return An {@link AuthResponse} indicating if the verification succeeded or if
     * further actions are required.
     */
    public AuthResponse verify(StepAuthRequest<EmailOtpVerificationPayload> stepAuthRequest) {
        return authHandler.handle(stepAuthRequest, emailOtpVerificationExecutor);
    }
}