package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when a requested authentication action is not supported by the
 * current authentication method.
 * <p>
 * For example, attempting to {@code GENERATE} the {@code password}
 * </p>
 * @see AuthError#AUTH_ACTION_NOT_SUPPORTED
 */
public class AuthActionNotSupportedException extends AuthException {

    /** The constant error mapping that binds this exception to a 400 Bad Request status. */
    private static final AuthError error = AuthError.AUTH_ACTION_NOT_SUPPORTED;

    /**
     * Constructs a new AuthActionNotSupportedException with a custom explanation.
     *
     * @param message A detail message explaining why the specific action was denied.
     */
    public AuthActionNotSupportedException(String message) {
        super(message, error);
    }
}