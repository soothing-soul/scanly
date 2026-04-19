package com.scanly.infra.notification.email.exception;

/**
 * Exception thrown when the notification system cannot find a suitable
 * mapper or strategy to process the provided email input.
 * <p>
 * This typically occurs within the {@code EmailMessageFactory} when a new
 * type of {@code EmailMessageInput} is introduced but has not yet been
 * registered with a corresponding content mapper.
 * <p>
 * Like other internal validation failures, this is mapped to
 * {@link MailError#EMAIL_CONTENT_MAPPER_NOT_FOUND} and should result
 * in an {@code INTERNAL_SERVER_ERROR}. It signals a missing piece of
 * configuration or a developer oversight in the notification pipeline.
 */
public class EmailContentMapperNotFoundException extends MailException {

    private static final MailError error = MailError.EMAIL_CONTENT_MAPPER_NOT_FOUND;

    /**
     * Constructs a new exception with a message identifying the missing mapper.
     *
     * @param message A description of the input type that lacked a mapper.
     */
    public EmailContentMapperNotFoundException(String message) {
        super(message, error);
    }
}