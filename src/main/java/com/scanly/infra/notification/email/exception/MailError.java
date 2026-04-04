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
     * Indicates that no {@code EmailBodyBuilder} has been registered for a requested {@code EmailPurpose}.
     * <p>
     * <b>Resolution:</b> Ensure a {@code @Component} class exists that implements
     * {@code EmailBodyBuilder} for the missing purpose.
     * </p>
     */
    EMAIL_BODY_BUILDER_NOT_FOUND("EMAIL_BODY_BUILDER_NOT_FOUND", HttpStatus.INTERNAL_SERVER_ERROR),

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
    MAIL_SENDING_FAILURE("MAIL_SENDING_FAILURE", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * Indicates a configuration conflict where multiple builders are assigned to the same {@code EmailPurpose}.
     * <p>
     * <b>Resolution:</b> Check the project for redundant builder implementations and
     * ensure each {@code EmailPurpose} is handled by exactly one builder.
     * </p>
     */
    DUPLICATE_EMAIL_BODY_BUILDER("DUPLICATE_EMAIL_BODY_BUILDER", HttpStatus.INTERNAL_SERVER_ERROR);

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