package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when a user provides a One-Time Password that does not
 * match the generated challenge, but the retry limit has not yet been exceeded.
 * <p>
 * In the context of the IAM authentication flow, this exception serves as
 * a "soft failure." It allows the security infrastructure to notify the user
 * that the entered code is incorrect while still permitting subsequent
 * attempts within the same {@code flowId}.
 * <p>
 * This exception maps to the {@link AuthError#INVALID_OTP} domain error.
 */
public class OtpInvalidException extends AuthException {

    /**
     * The specific domain error associated with a mismatched OTP input.
     */
    private static final AuthError error = AuthError.INVALID_OTP;

    /**
     * Constructs a new exception with a descriptive message.
     *
     * @param message The detail message explaining the verification failure.
     */
    public OtpInvalidException(String message) {
        super(message, error);
    }
}