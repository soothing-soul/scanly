package com.scanly.infra.notification.email.exception;

import com.scanly.infra.notification.email.EmailBodyBuilder;
import com.scanly.infra.notification.email.EmailPurpose;

/**
 * Exception thrown when the {@code MailService} cannot find a registered
 * {@link EmailBodyBuilder} for a given {@link EmailPurpose}.
 * <p>
 * This typically indicates a developer configuration error, such as forgetting
 * to implement a builder for a new email type or failing to register the
 * builder as a Spring bean.
 * </p>
 *
 * @see MailException
 * @see MailError#EMAIL_BODY_BUILDER_NOT_FOUND
 */
public class EmailBodyBuilderNotFoundException extends MailException {
    private static final MailError error = MailError.EMAIL_BODY_BUILDER_NOT_FOUND;
    /**
     * Constructs a new exception with a detailed error message.
     *
     * @param message The message describing the missing builder and its purpose.
     */
    public EmailBodyBuilderNotFoundException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a detailed error message and the
     * underlying cause of the failure.
     *
     * @param message The message describing the missing builder.
     * @param cause   The original cause of the exception.
     */
    public EmailBodyBuilderNotFoundException(String message, Throwable cause) {
        super(message, error, cause);
    }
}