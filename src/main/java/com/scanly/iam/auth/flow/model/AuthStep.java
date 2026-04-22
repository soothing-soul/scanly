package com.scanly.iam.auth.flow.model;

import com.scanly.iam.auth.common.model.AuthAction;
import com.scanly.iam.auth.common.model.AuthMethodType;
import java.util.List;

/**
 * Represents a single logical challenge within an authentication flow.
 * <p>
 * An {@code AuthStep} defines what methods are available to the user to satisfy
 * the current security requirement (e.g., Knowledge or Possession). It tracks
 * the user's progress from selecting a method to successfully verifying it.
 * </p>
 * @param allowedAuthMethods The set of valid {@link AuthMethodType}s the user can
 *                           choose from to complete this step.
 * @param currentAuthMethod  The specific method currently being attempted by the user.
 * @param status             The current progress of this step (PENDING, CHALLENGED, VERIFIED).
 * @param verifiedWith       The method that was ultimately successful in satisfying this step.
 */
public record AuthStep(
        List<AuthMethodType> allowedAuthMethods,
        AuthMethodType currentAuthMethod,
        AuthStepStatus status,
        AuthMethodType verifiedWith
) {
    /**
     * Convenience constructor to initialize a new, pending authentication step
     * with a list of available methods.
     *
     * @param allowedAuthMethods The methods permitted for this step.
     */
    public AuthStep(List<AuthMethodType> allowedAuthMethods) {
        this(
                allowedAuthMethods,
                null,
                AuthStepStatus.PENDING,
                null
        );
    }

    /**
     * Creates and returns a new AuthStep that is the logical next transition from the
     * current state of the {@link AuthStep} after applying the given {@link AuthAction}
     */
    public AuthStep update(AuthAction authAction, AuthMethodType authMethod) {
        switch (authAction) {
            case AuthAction.GENERATE:
                return new AuthStep(
                        allowedAuthMethods,
                        authMethod,
                        AuthStepStatus.CHALLENGED,
                        null
                );

            case VERIFY:
                return new AuthStep(
                        allowedAuthMethods,
                        authMethod,
                        AuthStepStatus.VERIFIED,
                        authMethod
                );

            default:
                return null;
        }
    }
}