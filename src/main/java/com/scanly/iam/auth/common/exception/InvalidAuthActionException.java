package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when the specified authentication action is not recognized
 * by the system.
 * <p>
 * This exception indicates a client-side error where the {@code currentAuthAction}
 * provided in the request does not match any of the supported actions defined
 * in the system's schema (e.g., "VERIFY", "RESEND", "CANCEL").
 * </p>
 * <p>
 * <b>Distinction:</b>
 * Unlike {@link AuthActionNotPermittedException}, which deals with actions that are
 * valid but restricted by state or policy, this exception identifies actions that
 * are <b>syntactically or logically undefined</b> for the current context.
 * </p>
 *
 * @see AuthError#INVALID_AUTH_ACTION
 */
public class InvalidAuthActionException extends AuthException {

    /** The constant error mapping that binds this exception to a 400 Bad Request status. */
    private static final AuthError error = AuthError.INVALID_AUTH_ACTION;

    /**
     * Constructs a new InvalidAuthActionException with a custom explanation.
     *
     * @param message A detail message explaining which action was unrecognized.
     */
    public InvalidAuthActionException(String message) {
        super(message, error);
    }
}