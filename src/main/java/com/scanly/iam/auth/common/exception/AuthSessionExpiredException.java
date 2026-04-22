package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when an authentication session has exceeded its validity period.
 * <p>
 * Authentication flows (especially those involving MFA) are typically short-lived
 * for security reasons. This exception indicates that the {@code flowId} provided
 * by the client has expired.
 * </p>
 * <p>
 * <b>Typical Handling:</b> When a client receives this error, it should navigate
 * the user back to the initial login screen to restart the primary authentication
 * process.
 * </p>
 * @see AuthError#AUTH_SESSION_EXPIRED
 */
public class AuthSessionExpiredException extends AuthException {

    /** The constant error mapping that binds this exception to a 401 Unauthorized status. */
    private static final AuthError error = AuthError.AUTH_SESSION_EXPIRED;

    /**
     * Constructs a new AuthSessionExpiredException with a custom explanation.
     *
     * @param message A detail message explaining why the session expired (e.g., "MFA timeout").
     */
    public AuthSessionExpiredException(String message) {
        super(message, error);
    }
}