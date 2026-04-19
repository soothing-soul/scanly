package com.scanly.infra.notification.email.message;

import org.springframework.stereotype.Component;
import java.util.List;

/**
 * A central factory responsible for orchestrating the creation of {@link EmailMessage} instances.
 * <p>
 * This factory acts as the glue between domain-specific data ({@link EmailContentInput}) and the
 * final message structure required by the notification engine. It utilizes a strategy pattern
 * by maintaining a list of {@link EmailContentMapper}s to find the appropriate translation
 * logic for any given input.
 * </p>
 *
 * @author Scanly Infrastructure Team
 */
@Component
public class EmailMessageFactory {

    /**
     * A collection of all available mappers registered in the Spring context.
     */
    private final List<EmailContentMapper> mappers;

    /**
     * Constructs the factory with the list of mappers discovered by Spring.
     *
     * @param mappers the list of {@link EmailContentMapper} implementations.
     */
    public EmailMessageFactory(List<EmailContentMapper> mappers) {
        this.mappers = mappers;
    }

    /**
     * Creates a fully populated {@link EmailMessage} from a raw content input.
     * <p>
     * This method resolves the correct mapper, executes the mapping to intermediate
     * {@link EmailContent}, and finally converts it into the deliverable message format.
     * </p>
     *
     * @param emailContentInput the domain-specific data (e.g., OTP details).
     * @return a structured {@link EmailMessage} ready for delivery.
     * @throws NullPointerException if no suitable mapper is found for the provided input.
     */
    public EmailMessage create(EmailContentInput emailContentInput) {
        EmailContentMapper mapper = getMapper(emailContentInput);
        EmailContent emailContent = mapper.map(emailContentInput);
        return toEmailMessage(emailContent);
    }

    /**
     * Searches the internal registry for a mapper that supports the given input type.
     *
     * @param emailContentInput the input to be mapped.
     * @return the supporting {@link EmailContentMapper}, or {@code null} if none is found.
     */
    private EmailContentMapper getMapper(EmailContentInput emailContentInput) {
        for (EmailContentMapper mapper : mappers) {
            if (mapper.supports(emailContentInput)) {
                return mapper;
            }
        }
        return null;
    }

    /**
     * Converts a rendered {@link EmailContent} DTO into a formal {@link EmailMessage}.
     *
     * @param emailContent the intermediate structured content.
     * @return the final {@link EmailMessage} implementation.
     */
    private EmailMessage toEmailMessage(EmailContent emailContent) {
        return new EmailMessage(
                emailContent.templateType().name(),
                emailContent.templateType(),
                emailContent.templateVariables()
        );
    }
}