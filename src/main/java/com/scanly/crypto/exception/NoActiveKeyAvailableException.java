package com.scanly.crypto.exception;

import com.scanly.crypto.manager.KeyLifecycleManager;

/**
 * Thrown when the platform attempts to perform a cryptographic operation (like signing)
 * but finds no keys in the vault with an {@code ACTIVE} status.
 * <p>
 * This exception indicates a critical readiness failure. It usually occurs during
 * the bootstrap phase if the {@link KeyLifecycleManager}
 * fails to load keys, or during a key rotation if all available keys have
 * been retired or revoked.
 * </p>
 * <p>
 * <b>System Impact:</b> When this exception is thrown, the platform cannot
 * issue new JWTs or sign analytics payloads. It essentially puts the
 * security layer into a "Read-Only" mode.
 * </p>
 */
public class NoActiveKeyAvailableException extends CryptoException {
    /** The standardized error definition for readiness failures. */
    private static final CryptoError error = CryptoError.NO_ACTIVE_KEY_AVAIlABLE;

    /**
     * Constructs a new exception with a message detailing the lack of active material.
     * @param message Internal details explaining the empty active key set.
     */
    public NoActiveKeyAvailableException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a message and an underlying cause.
     * @param message Contextual details.
     * @param cause   The root cause (e.g., an empty list during bootstrap).
     */
    public NoActiveKeyAvailableException(String message, Throwable cause) {
        super(message, error, cause);
    }
}