package com.scanly.infra.notification.email.render;

import com.scanly.infra.notification.email.message.EmailMessage;
import com.scanly.infra.notification.email.model.EmailTemplateType;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * Service responsible for rendering the final HTML or plaintext body of an email.
 * <p>
 * This component utilizes the Thymeleaf {@link TemplateEngine} to process
 * {@link EmailTemplateType} files. It dynamically injects variables provided in
 * an {@link EmailMessage} into the corresponding template to produce the
 * final string output for transmission.
 * </p>
 */
@Component
public class EmailBodyRenderer {

    /**
     * The Thymeleaf template engine used for processing templates and variables.
     */
    private final TemplateEngine templateEngine;

    /**
     * Constructs the renderer with the necessary template engine dependency.
     *
     * @param templateEngine the {@link TemplateEngine} configured within the application context.
     */
    public EmailBodyRenderer(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Renders the email body based on the template and variables defined in the message.
     *
     * @param emailMessage the {@link EmailMessage} containing the template type and data.
     * @return a {@code String} representing the fully rendered email content.
     */
    public String build(EmailMessage emailMessage) {
        EmailTemplateType template = emailMessage.getTemplate();
        Context context = buildContext(emailMessage);

        return templateEngine.process(template.getTemplateFileName(), context);
    }

    /**
     * Converts the map of template variables from the message into a Thymeleaf-specific context.
     * <p>
     * This method iterates through all entries in the {@code templateVariables} map
     * and registers them as variables accessible within the HTML template.
     * </p>
     *
     * @param emailMessage the source message containing the variable map.
     * @return a populated Thymeleaf {@link Context}.
     */
    private Context buildContext(EmailMessage emailMessage) {
        Context context = new Context();
        Map<String, String> templateVariables = emailMessage.getTemplateVariables();

        templateVariables.forEach(context::setVariable);

        return context;
    }
}