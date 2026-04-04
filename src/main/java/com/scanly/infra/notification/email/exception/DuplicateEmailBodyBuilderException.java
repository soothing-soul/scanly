package com.scanly.infra.notification.email.exception;

import com.scanly.infra.notification.email.EmailBodyBuilder;
import com.scanly.infra.notification.email.EmailPurpose;

/**
 * Exception thrown when multiple {@link EmailBodyBuilder} implementations are detected
 * for the same {@link EmailPurpose}.
 * <p>
 * This exception serves as a "Fail-Fast" mechanism during application startup.
 * Since the {@code MailService} relies on a 1:1 mapping between a purpose and a builder,
 * a duplicate would lead to non-deterministic behavior.
 * </p>
 *
 * @author Scanly Infrastructure Team
 * @see MailException
 * @see MailError#DUPLICATE_EMAIL_BODY_BUILDER
 */
public class DuplicateEmailBodyBuilderException extends MailException {
    private static final MailError error = MailError.DUPLICATE_EMAIL_BODY_BUILDER;

    /**
     * Constructs a new exception with a message identifying the conflicting email purpose.
     *
     * @param message A message detailing the specific {@link EmailPurpose} that has duplicate builders.
     */
    public DuplicateEmailBodyBuilderException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a message and the underlying cause.
     *
     * @param message A message detailing the conflict.
     * @param cause   The original cause of the exception.
     */
    public DuplicateEmailBodyBuilderException(String message, Throwable cause) {
        super(message, error, cause);
    }
}