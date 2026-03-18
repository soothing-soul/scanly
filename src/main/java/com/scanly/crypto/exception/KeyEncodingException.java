package com.scanly.crypto.exception;

import java.security.spec.InvalidKeySpecException;

/**
 * Thrown when key material is successfully retrieved but cannot be decoded
 * or parsed into a valid cryptographic format.
 * <p>
 * This exception typically occurs during the transition from raw file content
 * (PEM/Base64) to Java Cryptography Architecture (JCA) objects. It signals
 * that the input data does not conform to expected standards such as
 * PKCS#8 (for private keys) or X.509 (for public keys).
 * </p>
 * <p>
 * <b>Common Causes:</b>
 * <ul>
 * <li>Missing or malformed PEM headers/footers.</li>
 * <li>Invalid Base64 encoding within the key file.</li>
 * <li>A mismatch between the key material and the specified algorithm.</li>
 * </ul>
 * </p>
 */
public class KeyEncodingException extends CryptoException {

    /** The standardized error definition for encoding and parsing failures. */
    private static final CryptoError error = CryptoError.KEY_ENCODING_FAILURE;

    /**
     * Constructs a new exception with a specific formatting error message.
     * @param message Details regarding the encoding discrepancy.
     */
    public KeyEncodingException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a message and the underlying parsing cause.
     * @param message Contextual details.
     * @param cause   The root cause (e.g., {@link InvalidKeySpecException}).
     */
    public KeyEncodingException(String message, Throwable cause) {
        super(message, error, cause);
    }
}