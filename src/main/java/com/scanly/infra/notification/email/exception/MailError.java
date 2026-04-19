package com.scanly.infra.notification.email.exception;

import com.scanly.common.exception.ScanlyError;
import org.springframework.http.HttpStatus;

/**
 * Enumeration of specific error types encountered within the Scanly Email Infrastructure.
 * <p>
 * This enum implements {@link ScanlyError} to provide a standardized set of
 * error codes and HTTP statuses. These constants are used by {@link MailException}
 * and its subclasses to ensure consistent error reporting across the platform.
 * </p>
 *
 * @see MailException
 * @see ScanlyError
 */
public enum MailError implements ScanlyError {
    /**
     * Error code indicating that an email request was malformed or failed internal
     * validation requirements.
     * <p>
     * This is treated as an {@code INTERNAL_SERVER_ERROR} because the creation of
     * email requests is an internal process. A failure here suggests a regression
     * or logic error in the upstream service (e.g., the Auth Generation Executor)
     * rather than a client-side mistake.
     */
    INVALID_EMAIL_REQUEST("INVALID_EMAIL_REQUEST", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * Error code indicating that the system failed to resolve a strategy or mapper
     * for the provided {@code EmailContentInput}.
     * <p>
     * In our strategy-based notification architecture, each message type (e.g., OTP,
     * Welcome Email) must have a corresponding mapper registered in the
     * {@code EmailMessageFactory}. This error is thrown when a message type is
     * requested but no implementation exists to handle its transformation into
     * {@code EmailMessage} content.
     * <p>
     * <b>Classification:</b> {@code INTERNAL_SERVER_ERROR} (500).
     * <p>
     * This is strictly a developer-facing error. It signals a missing configuration
     * or an incomplete implementation of a new notification type. It should be
     * monitored via internal logs to catch regressions during the integration of
     * new domain events.
     */
    EMAIL_CONTENT_MAPPER_NOT_FOUND("EMAIL_CONTENT_MAPPER_NOT_FOUND", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * Indicates a failure during the technical assembly of the {@code MimeMessage}.
     * <p>
     * This occurs during the hand-off between the generated HTML body and the
     * Jakarta Mail API. Possible causes include invalid email headers,
     * unsupported character encodings, or illegal recipient formats.
     * </p>
     * <p>
     * <b>Resolution:</b> Inspect the underlying {@code MessagingException} and
     * validate the {@code MailProperties} and recipient data.
     * </p>
     */
    MIME_MESSAGE_GENERATION_FAILURE("MIME_MESSAGE_GENERATION_FAILURE", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * Indicates a failure during the physical transmission of the email via SMTP.
     * <p>
     * This error occurs during the {@code mailSender.send()} phase. Common causes
     * include network timeouts, SMTP authentication failures, or being
     * rate-limited by the email service provider.
     * </p>
     * <p>
     * <b>Resolution:</b> Check SMTP configuration, network egress rules,
     * and provider status pages.
     * </p>
     */
    MAIL_SENDING_FAILURE("MAIL_SENDING_FAILURE", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String errorCode;
    private final HttpStatus httpStatus;

    MailError(String errorCode, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * @return The unique string identifier for the error.
     */
    @Override
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @return The {@link HttpStatus} associated with this error type.
     */
    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}