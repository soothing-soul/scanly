package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when the requested authentication method is not recognized or supported
 * by the IAM system.
 * <p>
 * This typically indicates a client-side error where the {@code authMethod}
 * string does not map to any known provider (e.g., "SMS", "TOTP", "WEBAUTHN").
 * It is also used if a method is theoretically supported by the platform but has
 * not been configured or instantiated in the current environment.
 * </p>
 * <p>
 * <b>Distinction:</b>
 * Unlike {@link AuthMethodNotPermittedException}, which refers to a valid method
 * that the user is restricted from using, this exception indicates that the
 * requested method <b>does not exist</b> within the system's registry.
 * </p>
 *
 * @see AuthError#INVALID_AUTH_METHOD
 */
public class InvalidAuthMethodException extends AuthException {

    /** The constant error mapping that binds this exception to a 400 Bad Request status. */
    private static final AuthError error = AuthError.INVALID_AUTH_METHOD;

    /**
     * Constructs a new InvalidAuthMethodException with a custom explanation.
     *
     * @param message A detail message explaining why the method was considered invalid.
     */
    public InvalidAuthMethodException(String message) {
        super(message, error);
    }
}