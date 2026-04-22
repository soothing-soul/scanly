package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when a user attempts to execute an authentication step that has
 * already been successfully finalized.
 * <p>
 * This is primarily used to enforce the <b>idempotency</b> and <b>integrity</b>
 * of the authentication flow. It prevents "replay" attempts where a client might
 * try to submit the same credentials or OTP multiple times after the flow has
 * moved forward.
 * </p>
 * <p>
 * <b>Common Scenarios:</b>
 * <ul>
 * <li>A user double-clicks a "Verify" button.</li>
 * <li>A user hits the "Back" button in a browser after completing an MFA challenge.</li>
 * <li>A race condition where two simultaneous requests attempt to satisfy the same step.</li>
 * </ul>
 * </p>
 * @see AuthError#AUTH_STEP_ALREADY_COMPLETED
 */
public class AuthStepAlreadyCompletedException extends AuthException {

    /** The constant error mapping that binds this exception to a 409 Conflict status. */
    private static final AuthError error = AuthError.AUTH_STEP_ALREADY_COMPLETED;

    /**
     * Constructs a new AuthStepAlreadyCompletedException with a custom explanation.
     *
     * @param message A detail message explaining which step was already finished.
     */
    public AuthStepAlreadyCompletedException(String message) {
        super(message, error);
    }
}