package com.scanly.infra.notification.email.exception;

import jakarta.mail.MessagingException;

/**
 * Exception thrown when a rendered email fails to be transmitted via the SMTP server.
 * <p>
 * This exception represents a "Transmission Phase" failure. It typically wraps
 * low-level exceptions from the mail provider or the Jakarta Mail API, such as
 * {@link MessagingException} or Spring's {@code MailSendException}.
 * </p>
 * <p>
 * Unlike generation failures, this error usually stems from environmental factors
 * (e.g., network timeouts, authentication issues, or rate limiting) rather than
 * code logic.
 * </p>
 *
 * @author Scanly Infrastructure Team
 * @see MailException
 * @see MailError#MAIL_SENDING_FAILURE
 */
public class MailSendingFailedException extends MailException {

    private static final MailError error = MailError.MAIL_SENDING_FAILURE;

    /**
     * Constructs a new exception with a descriptive message.
     *
     * @param message The details of the sending failure.
     */
    public MailSendingFailedException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a message and the underlying cause.
     * <p>
     * <b>Note:</b> It is highly recommended to pass the original cause
     * (e.g., {@code MessagingException}) to assist in troubleshooting
     * SMTP-level errors.
     * </p>
     *
     * @param message The details of the sending failure.
     * @param cause   The original exception thrown by the mail sender.
     */
    public MailSendingFailedException(String message, Throwable cause) {
        super(message, error, cause);
    }
}