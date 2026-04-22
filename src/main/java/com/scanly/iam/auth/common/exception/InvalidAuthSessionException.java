package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when an authentication session does not exist in the system.
 * <p>
 * Authentication flows (especially those involving MFA) are typically short-lived
 * for security reasons. This exception indicates that the {@code flowId} provided
 * by the client does not exist in the store anymore. Possible reasons include
 * <ul>
 *     <li>
 *         {@code flowId} is not valid
 *     </li>
 *     <li>
 *         {@code flowId} existed before, but has expired now.
 *     </li>
 *
 * </ul>
 *
 * </p>
 * <p>
 * <b>Typical Handling:</b> When a client receives this error, it should navigate
 * the user back to the initial login screen to restart the primary authentication
 * process.
 * </p>
 * @see AuthError#INVALID_AUTH_SESSION
 */
public class InvalidAuthSessionException extends AuthException {

    /** The constant error mapping that binds this exception to a 400 BadRequest status. */
    private static final AuthError error = AuthError.INVALID_AUTH_SESSION;

    /**
     * Constructs a new InvalidAuthSessionException with a custom explanation.
     *
     * @param message A detail message explaining why the session is invalid (e.g., "MFA timeout").
     */
    public InvalidAuthSessionException(String message) {
        super(message, error);
    }
}