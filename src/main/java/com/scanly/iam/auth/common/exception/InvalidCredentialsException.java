package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when the provided credentials (e.g., username, password, or
 * primary identification factors) do not match any records in the system.
 * <p>
 * This is the standard exception for failed login attempts.
 * </p>
 * <p>
 * <b>Security Note:</b> Detailed failure reasons (like "Password expired" or
 * "Account locked") should typically use their own specific exceptions rather
 * than this generic credential failure to provide a better user experience
 * while maintaining security boundaries.
 * </p>
 *
 * @see AuthError#INVALID_CREDENTIALS
 */
public class InvalidCredentialsException extends AuthException {

    /** The constant error mapping that binds this exception to a 401 Unauthorized status. */
    private static final AuthError error = AuthError.INVALID_CREDENTIALS;

    /**
     * Constructs a new InvalidCredentialsException with a custom explanation.
     *
     * @param message A detail message (usually logged internally) explaining the failure.
     */
    public InvalidCredentialsException(String message) {
        super(message, error);
    }
}