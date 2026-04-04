package com.scanly.infra.notification.email.exception;

import jakarta.mail.MessagingException;

/**
 * Exception thrown when the system fails to construct a valid {@code MimeMessage}.
 * <p>
 * This exception typically wraps a {@link MessagingException} encountered
 * during the assembly of email headers, recipient lists, or the HTML body. It indicates
 * a failure in the "Packaging" phase of the email lifecycle rather than a
 * "Content" or "Transmission" failure.
 * </p>
 *
 * @author Scanly Infrastructure Team
 * @see MailException
 * @see MailError
 */
public class MimeMessageGenerationFailedException extends MailException {
    private static final MailError error = MailError.MIME_MESSAGE_GENERATION_FAILURE;

    /**
     * Constructs a new exception with a specific error message.
     *
     * @param message The descriptive message explaining why the generation failed.
     */
    public MimeMessageGenerationFailedException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a message and the underlying cause.
     * <p>
     * This is the preferred constructor as it allows preservation of the
     * {@link MessagingException} stack trace for easier debugging.
     * </p>
     *
     * @param message The descriptive message explaining the failure.
     * @param cause   The underlying {@link Throwable} (usually a MessagingException).
     */
    public MimeMessageGenerationFailedException(String message, Throwable cause) {
        super(message, error, cause);
    }
}