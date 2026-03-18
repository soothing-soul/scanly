package com.scanly.crypto.exception;

/**
 * Exception thrown when a cryptographic verification process fails due to
 * the unavailability of a valid, active key.
 * <p>
 * This exception maps directly to {@link CryptoError#VERIFICATION_FAILED},
 * providing a clear diagnostic path for system administrators to identify
 * key rotation or expiration issues.
 * </p>
 */
public class VerificationFailedException extends CryptoException {

    /** The standardized error code for missing/inactive keys. */
    private static final CryptoError error = CryptoError.VERIFICATION_FAILED;

    /**
     * Constructs a new exception with a specific detail message.
     * @param message A descriptive message explaining why the key was unavailable.
     */
    public VerificationFailedException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a detail message and the underlying cause.
     * @param message A descriptive message.
     * @param cause   The underlying throwable (e.g., a KeyStoreException).
     */
    public VerificationFailedException(String message, Throwable cause) {
        super(message, error, cause);
    }
}