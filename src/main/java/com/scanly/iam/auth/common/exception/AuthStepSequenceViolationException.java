package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when an authentication request is made out of the expected order.
 * <p>
 * This exception enforces the integrity of the multistep authentication state machine.
 * It is triggered when a client attempts to execute a step (e.g., verifying an MFA code)
 * before the prerequisite steps (e.g., validating primary credentials) have been satisfied.
 * </p>
 * <p>
 * <b>Security Note:</b> This acts as a critical defense against "step-skipping" attacks
 * where an actor might try to bypass early security layers by directly hitting
 * later-stage endpoints.
 * </p>
 *
 * @see AuthError#AUTH_STEP_SEQUENCE_VIOLATED
 */
public class AuthStepSequenceViolationException extends AuthException {

    /** The constant error mapping that binds this exception to a 400 Bad Request status. */
    private static final AuthError error = AuthError.AUTH_STEP_SEQUENCE_VIOLATED;

    /**
     * Constructs a new AuthStepSequenceViolationException with a custom explanation.
     *
     * @param message A detail message explaining which prerequisite step was missed.
     */
    public AuthStepSequenceViolationException(String message) {
        super(message, error);
    }
}