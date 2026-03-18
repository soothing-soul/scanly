package com.scanly.crypto.exception;

import java.security.NoSuchAlgorithmException;

/**
 * Thrown when a requested cryptographic algorithm is not supported or available
 * in the current Java Runtime Environment (JRE).
 * <p>
 * This exception typically indicates a configuration issue or a limitation of the
 * underlying security provider. Since algorithms like SHA-256 and ECDSA are
 * standard in modern JVMs, this exception often signals a catastrophic
 * environment failure.
 * </p>
 */
public class AlgorithmNotFoundException extends CryptoException {

    /** The standardized error definition for algorithm failures. */
    private static final CryptoError error = CryptoError.ALGORITHM_NOT_FOUND;

    /**
     * Constructs a new exception with a specific error message.
     * @param message Descriptive details about which algorithm was missing.
     */
    public AlgorithmNotFoundException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a message and the underlying cause.
     * @param message Descriptive details about the failure.
     * @param cause   The original {@link NoSuchAlgorithmException}.
     */
    public AlgorithmNotFoundException(String message, Throwable cause) {
        super(message, error, cause);
    }
}