package com.scanly.crypto.exception;

import java.security.SignatureException;

/**
 * A general-purpose exception for unexpected failures within the cryptographic engine.
 * <p>
 * This exception serves as a wrapper for low-level, checked JCA exceptions that
 * occur during active operations such as signing, verifying, or encrypting. It
 * indicates that the system understands the request and possesses the necessary
 * material, but a runtime failure occurred during execution.
 * </p>
 * <p>
 * <b>Debugging Tip:</b> Always check the underlying {@code cause} of this exception
 * in the logs to identify specific JCA provider failures.
 * </p>
 */
public class InternalCryptoException extends CryptoException {

    /** The standardized error definition for general crypto failures. */
    private static final CryptoError error = CryptoError.INTERNAL_CRYPTO_ERROR;

    /**
     * Constructs a new exception with a specific error message.
     * @param message Internal details regarding the failure point.
     */
    public InternalCryptoException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception by wrapping a low-level cryptographic cause.
     * @param message Contextual information about the operation that failed.
     * @param cause   The original exception (e.g., {@link SignatureException}).
     */
    public InternalCryptoException(String message, Throwable cause) {
        super(message, error, cause);
    }
}