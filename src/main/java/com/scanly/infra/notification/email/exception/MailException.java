package com.scanly.infra.notification.email.exception;

import com.scanly.common.exception.ScanlyBaseException;
import com.scanly.common.exception.ScanlyError;

/**
 * Abstract base class for all exceptions thrown by the Scanly Email Infrastructure.
 * <p>
 * This class extends {@link ScanlyBaseException} to integrate with the global
 * platform error handling strategy. It is marked {@code abstract} to ensure that
 * only specific, descriptive sub-exceptions (e.g., {@code EmailBodyBuilderNotFoundException})
 * are instantiated and thrown.
 * </p>
 *
 * @see ScanlyBaseException
 * @see ScanlyError
 */
public abstract class MailException extends ScanlyBaseException {

    /**
     * Constructs a new MailException with a specific message and error code.
     *
     * @param message A descriptive message explaining the failure.
     * @param error   The platform-specific error definition (code, status, etc.).
     */
    protected MailException(String message, ScanlyError error) {
        super(message, error);
    }

    /**
     * Constructs a new MailException with a message, error code, and the underlying cause.
     * <p>
     * Use this constructor when wrapping checked exceptions (like {@code MessagingException})
     * to preserve the original stack trace for troubleshooting.
     * </p>
     *
     * @param message A descriptive message explaining the failure.
     * @param error   The platform-specific error definition.
     * @param cause   The underlying cause of the exception.
     */
    protected MailException(String message, ScanlyError error, Throwable cause) {
        super(message, error, cause);
    }
}