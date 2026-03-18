package com.scanly.crypto.exception;

import java.io.IOException;

/**
 * Thrown when the platform fails to retrieve raw key material from its source.
 * <p>
 * This exception is primarily associated with infrastructure and I/O failures.
 * It occurs before any cryptographic parsing takes place, typically during
 * the initial file-read or network-fetch operation.
 * </p>
 * <p>
 * <b>Common Scenarios:</b>
 * <ul>
 * <li>A specified filesystem path does not exist.</li>
 * <li>The application lacks the necessary read permissions for the key files.</li>
 * <li>A network-based key source (like S3 or Vault) is unreachable.</li>
 * </ul>
 * </p>
 */
public class KeyLoadingException extends CryptoException {

    /** The standardized error definition for I/O and retrieval failures. */
    private static final CryptoError error = CryptoError.KEY_LOADING_FAILURE;

    /**
     * Constructs a new exception with a specific loading error message.
     * @param message Details regarding the I/O failure or missing path.
     */
    public KeyLoadingException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a message and the underlying I/O cause.
     * @param message Contextual details about the attempted retrieval.
     * @param cause   The root cause (typically a {@link IOException}).
     */
    public KeyLoadingException(String message, Throwable cause) {
        super(message, error, cause);
    }
}