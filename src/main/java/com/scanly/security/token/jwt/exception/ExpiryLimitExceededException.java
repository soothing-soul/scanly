package com.scanly.security.token.jwt.exception;

import com.scanly.security.token.jwt.config.JwtProperties;

/**
 * Exception thrown when a requested JWT expiry duration exceeds the platform's
 * maximum allowed limit.
 * <p>
 * This exception acts as a security guard, ensuring that the system does
 * not issue "immortal" or excessively long-lived tokens that could pose a
 * security risk if compromised.
 * </p>
 * * @see {@link JwtProperties#getMaxTimeToLive}
 */
public class ExpiryLimitExceededException extends JwtException {
    /** The standardized Scanly error definition associated with an
     * expiry policy breach.
     */
    private static final JwtError error = JwtError.EXPIRY_LIMIT_BREACH;

    /**
     * Constructs a new exception with a specific error message.
     *
     * @param message A descriptive message explaining the limit breach
     * (e.g., "Requested TTL of 24h exceeds limit of 12h").
     */
    public ExpiryLimitExceededException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a specific error message and
     * the underlying cause.
     *
     * @param message Descriptive error message.
     * @param cause   The underlying throwable that caused this failure.
     */
    public ExpiryLimitExceededException(String message, Throwable cause) {
        super(message, error, cause);
    }
}