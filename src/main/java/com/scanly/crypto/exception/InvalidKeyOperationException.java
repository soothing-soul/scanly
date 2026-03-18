package com.scanly.crypto.exception;

import com.scanly.crypto.model.KeyStatus;

/**
 * Thrown when a cryptographic operation is attempted that violates the
 * current lifecycle state or intended usage of a key.
 * <p>
 * This exception is a critical security guardrail. It is triggered when
 * a key exists in the vault but its {@link KeyStatus}
 * prohibits the requested action—for example, attempting to generate a
 * new signature using a key marked as {@code EXPIRED} or {@code VERIFY_ONLY}.
 * </p>
 * <p>
 * <b>Audit Significance:</b> Repeated occurrences of this exception may
 * indicate an out-of-sync key rotation process or an attempted misuse
 * of retired cryptographic material.
 * </p>
 */
public class InvalidKeyOperationException extends CryptoException {
    /** The standardized error definition for lifecycle policy violations. */
    private static final CryptoError error = CryptoError.INVALID_KEY_OPERATION;
    /**
     * Constructs a new exception with a specific policy violation message.
     * @param message Details on why the key status blocked the operation.
     */
    public InvalidKeyOperationException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a message and an underlying cause.
     * @param message Contextual details.
     * @param cause   The root cause of the state conflict.
     */
    public InvalidKeyOperationException(String message, Throwable cause) {
        super(message, error, cause);
    }
}