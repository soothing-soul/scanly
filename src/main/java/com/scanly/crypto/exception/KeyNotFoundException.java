package com.scanly.crypto.exception;

import com.scanly.crypto.api.KeyVault;
import com.scanly.crypto.model.KeyPairContainer;

/**
 * Thrown when a requested Key ID (kid) cannot be resolved within the
 * {@link KeyVault}.
 * <p>
 * This exception indicates a lookup failure. It occurs when the platform
 * attempts to verify a signature or retrieve public key material using
 * an identifier that does not match any known {@link KeyPairContainer}
 * currently held in memory.
 * </p>
 * <p>
 * <b>Common Scenarios:</b>
 * <ul>
 * <li>A JWT is presented with a 'kid' header that has been rotated out of the vault.</li>
 * <li>An incorrect 'kid' was provided during a manual key retrieval request.</li>
 * <li>A synchronization delay between the Key Loader and the Vault during startup.</li>
 * </ul>
 * </p>
 */
public class KeyNotFoundException extends CryptoException {
    /** The standardized error definition for key resolution failures. */
    private static final CryptoError error = CryptoError.KEY_NOT_FOUND;

    /**
     * Constructs a new exception with a message identifying the missing key.
     * @param message Details including the specific 'kid' that was not found.
     */
    public KeyNotFoundException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a message and an underlying cause.
     * @param message Contextual information.
     * @param cause   The root cause of the lookup failure.
     */
    public KeyNotFoundException(String message, Throwable cause) {
        super(message, error, cause);
    }
}