package com.scanly.security.token.jwt.exception;

/**
 * Exception thrown when a JWT operation requests a cryptographic algorithm
 * that is not supported or configured within the Scanly platform.
 * <p>
 * This typically occurs during Token Decoding: When a received token's {@code alg}
 * header does not match the platform's allowed algorithm set
 * (e.g., expecting ES256 but receiving HS256).
 * </p>
 * This exception maps to {@link JwtError#ALGORITHM_NOT_SUPPORTED} and results
 * in an Internal Server Error (500), as it indicates a configuration or implementation mismatch.
 */
public class AlgorithmNotSupportedException extends JwtException {

    /**
     * The fixed error category for algorithm mismatch failures.
     */
    private static final JwtError error = JwtError.ALGORITHM_NOT_SUPPORTED;

    /**
     * Constructs a new exception with a specific detail message.
     *
     * @param message Explains which algorithm was requested and why it is unsupported.
     */
    public AlgorithmNotSupportedException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a message and the underlying root cause.
     *
     * @param message Explains the failure context.
     * @param cause   The original exception (e.g., a {@code NoSuchAlgorithmException}
     * from the JCA) that triggered this failure.
     */
    public AlgorithmNotSupportedException(String message, Throwable cause) {
        super(message, error, cause);
    }
}
