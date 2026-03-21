package com.scanly.security.token.jwt.exception;

import com.scanly.common.exception.ScanlyBaseException;
import com.scanly.common.exception.ScanlyError;

/**
 * Base exception for all errors occurring within the Scanly JWT infrastructure.
 * <p>
 * This class serves as a specialized parent for more granular JWT exceptions,
 * ensuring that all token-related failures can be handled uniformly by the
 * {@code ControllerAdvice} or other error-handling mechanisms.
 * </p>
 * * It extends {@link ScanlyBaseException} to integrate with the global platform
 * error reporting and tracing systems.
 */
public abstract class JwtException extends ScanlyBaseException {

    /**
     * Constructs a new JwtException with a descriptive message and a specific error code.
     *
     * @param message A detailed explanation of the failure.
     * @param error   The {@link ScanlyError} representing the specific error category and code.
     */
    protected JwtException(String message, ScanlyError error) {
        super(message, error);
    }

    /**
     * Constructs a new JwtException with a descriptive message, an error code,
     * and the underlying cause of the failure.
     *
     * @param message A detailed explanation of the failure.
     * @param error   The {@link ScanlyError} representing the error category.
     * @param cause   The original exception (e.g., a Nimbus {@code JOSEException})
     * that triggered this failure.
     */
    protected JwtException(String message, ScanlyError error, Throwable cause) {
        super(message, error, cause);
    }
}