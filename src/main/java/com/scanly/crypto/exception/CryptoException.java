package com.scanly.crypto.exception;

import com.scanly.common.exception.ScanlyBaseException;
import com.scanly.common.exception.ScanlyError;

/**
 * The base exception for all cryptographic failures within the Scanly platform.
 * <p>
 * This abstract-style base class integrates the {@code crypto} package with the
 * global application error-handling strategy. By requiring a {@link ScanlyError},
 * it ensures that every cryptographic failure can be mapped to a consistent
 * API response code and message for the frontend.
 * </p>
 * <p>
 * <b>Usage:</b> This class should not be thrown directly. Instead, extend it
 * to create specific exceptions for logic-driven failures (e.g., status mismatches)
 * or infrastructure-driven failures (e.g., JCA provider issues).
 * </p>
 */
public abstract class CryptoException extends ScanlyBaseException {

    /**
     * Constructs a new exception with a specific error context.
     *
     * @param message A descriptive message for internal logging.
     * @param error   The standardized {@link ScanlyError} for API mapping.
     */
    protected CryptoException(String message, ScanlyError error) {
        super(message, error);
    }

    /**
     * Constructs a new exception with an underlying cause for deep-trace debugging.
     *
     * @param message A descriptive message for internal logging.
     * @param error   The standardized {@link ScanlyError} for API mapping.
     * @param cause   The original exception (e.g., {@link java.security.SignatureException}).
     */
    protected CryptoException(String message, ScanlyError error, Throwable cause) {
        super(message, error, cause);
    }
}