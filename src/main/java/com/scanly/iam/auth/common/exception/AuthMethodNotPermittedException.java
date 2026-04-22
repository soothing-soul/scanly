package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when a user attempts to use an authentication method that is not
 * permitted for their account, security group, or current environmental context.
 * <p>
 * This differs from {@link InvalidAuthMethodException} in that the method itself
 * may be valid within the system, but is <b>prohibited</b> for this specific request.
 * Examples include:
 * <ul>
 * <li>A user attempting to use SMS-OTP when their policy requires a Hardware Security Key.</li>
 * <li>An administrative account attempting to use a "weak" auth method from an untrusted IP.</li>
 * <li>Attempting to use a method that the user has not yet enrolled in.</li>
 * </ul>
 * </p>
 * @see AuthError#AUTH_METHOD_NOT_PERMITTED
 */
public class AuthMethodNotPermittedException extends AuthException {

    /** The constant error mapping that binds this exception to a 400 Bad Request status. */
    private static final AuthError error = AuthError.AUTH_METHOD_NOT_PERMITTED;

    /**
     * Constructs a new AuthMethodNotPermittedException with a custom explanation.
     *
     * @param message A detail message explaining why the chosen method was restricted.
     */
    public AuthMethodNotPermittedException(String message) {
        super(message, error);
    }
}