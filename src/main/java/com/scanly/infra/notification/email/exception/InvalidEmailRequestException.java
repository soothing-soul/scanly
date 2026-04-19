package com.scanly.infra.notification.email.exception;

import com.scanly.infra.notification.email.model.EmailRequest;

/**
 * Exception thrown when an internal request to send an email fails validation.
 * <p>
 * This exception indicates a contract violation between the domain services
 * (e.g., {@code EmailOtpGenerationExecuter}) and the notification infrastructure.
 * Since email requests are generated internally and not directly from user input,
 * this failure typically suggests a logic error in how the {@link EmailRequest}
 * was constructed.
 * <p>
 * It maps to the {@link MailError#INVALID_EMAIL_REQUEST} code, facilitating
 * clear logging and internal alerting for system health monitoring.
 */
public class InvalidEmailRequestException extends MailException {
    private static final MailError error = MailError.INVALID_EMAIL_REQUEST;

    /**
     * Constructs a new exception with a specific error message.
     *
     * @param message Detailed information about the validation failure
     * (e.g., missing recipient or malformed template data).
     */
    public InvalidEmailRequestException(String message) {
        super(message, error);
    }
}