package com.scanly.infra.notification.email.message;

import com.scanly.infra.notification.common.NotificationMessage;
import com.scanly.infra.notification.email.model.EmailTemplateType;

import java.util.Map;

/**
 * A specialized implementation of {@link NotificationMessage} for template-based email content.
 * <p>
 * This class encapsulates the data required to render and send a rich email, including
 * the subject line, the specific template to be used, and a map of dynamic variables
 * to be injected into that template.
 * </p>
 */
public class EmailMessage implements NotificationMessage {

    /**
     * The subject line of the email.
     */
    private final String subject;

    /**
     * The specific {@link EmailTemplateType} that defines the visual layout and
     * structure of the email.
     */
    private final EmailTemplateType template;

    /**
     * A map containing the key-value pairs used to populate placeholders within
     * the selected template (e.g., {@code "userName" -> "Alice"}).
     */
    private final Map<String, String> templateVariables;

    /**
     * Package-private constructor for creating an email message.
     * <p>
     * Use a dedicated factory to instantiate this class in external packages.
     * </p>
     *
     * @param subject           the email subject.
     * @param template          the {@link EmailTemplateType} to be rendered.
     * @param templateVariables the data to be injected into the template.
     */
    EmailMessage(String subject, EmailTemplateType template, Map<String, String> templateVariables) {
        this.subject = subject;
        this.template = template;
        this.templateVariables = templateVariables;
    }

    /**
     * Retrieves the email subject line.
     *
     * @return the subject string.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Retrieves the template type assigned to this message.
     *
     * @return the {@link EmailTemplateType} instance.
     */
    public EmailTemplateType getTemplate() {
        return template;
    }

    /**
     * Retrieves the dynamic variables for template injection.
     *
     * @return an unmodifiable or standard map of template keys and their corresponding values.
     */
    public Map<String, String> getTemplateVariables() {
        return templateVariables;
    }
}