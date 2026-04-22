package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when an OTP verification is attempted for a challenge that does not exist.
 * <p>
 * This typically occurs if the {@code challengeId} (flowId) provided by the client
 * is incorrect, or if the OTP record has been evicted from the cache due to
 * expiration or a previous successful verification.
 * <p>
 * This exception maps directly to the {@link AuthError#OTP_NOT_FOUND} error code,
 * allowing the global exception handler to return a consistent 404 or 400
 * response to the client.
 */
public class OtpNotFoundException extends AuthException {

    /**
     * The specific domain error associated with this exception.
     */
    private static final AuthError error = AuthError.OTP_NOT_FOUND;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message The detail message explaining the context of the missing OTP.
     */
    public OtpNotFoundException(String message) {
        super(message, error);
    }
}