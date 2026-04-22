package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when a requested authentication action is logically invalid for the
 * current state of the authentication flow.
 * <p>
 * Unlike an authorization error, this exception indicates that the <b>action itself</b>
 * cannot be performed at this time. For example, attempting to {@code VERIFY} the OTP before
 * {@code GENERATE}
 * </p>
 * @see AuthError#AUTH_ACTION_NOT_PERMITTED
 */
public class AuthActionNotPermittedException extends AuthException {

    /** The constant error mapping that binds this exception to a 400 Bad Request status. */
    private static final AuthError error = AuthError.AUTH_ACTION_NOT_PERMITTED;

    /**
     * Constructs a new AuthActionNotPermittedException with a custom explanation.
     *
     * @param message A detail message explaining why the specific action was denied.
     */
    public AuthActionNotPermittedException(String message) {
        super(message, error);
    }
}