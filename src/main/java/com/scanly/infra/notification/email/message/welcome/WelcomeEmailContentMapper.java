package com.scanly.infra.notification.email.message.welcome;

import com.scanly.infra.notification.email.message.EmailContent;
import com.scanly.infra.notification.email.message.EmailContentInput;
import com.scanly.infra.notification.email.message.EmailContentMapper;
import com.scanly.infra.notification.email.model.EmailTemplateType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation of {@link EmailContentMapper} for "Welcome" notifications.
 * <p>
 * This component handles the transformation of {@link WelcomeEmailInput} into the
 * structured {@link EmailContent} required for onboarding new users. It defines
 * the static subject line "Welcome to Scanly" and selects the
 * {@link EmailTemplateType#WELCOME} template.
 * </p>
 */
@Component
public class WelcomeEmailContentMapper implements EmailContentMapper {

    /**
     * Checks if the provided input is an instance of {@link WelcomeEmailInput}.
     *
     * @param emailContentInput the input data to evaluate.
     * @return {@code true} if the input is a {@link WelcomeEmailInput}; {@code false} otherwise.
     */
    @Override
    public boolean supports(EmailContentInput emailContentInput) {
        return emailContentInput instanceof WelcomeEmailInput;
    }

    /**
     * Maps the welcome-specific input into generic email content.
     * <p>
     * Sets the fixed subject line for onboarding and prepares the template variables
     * based on the provided user profile data.
     * </p>
     *
     * @param emailContentInput the welcome input data containing user details.
     * @return a fully populated {@link EmailContent} instance.
     */
    @Override
    public EmailContent map(EmailContentInput emailContentInput) {
        WelcomeEmailInput welcome = (WelcomeEmailInput) emailContentInput;
        return new EmailContent(
                EmailTemplateType.WELCOME,
                getTemplateVariables(welcome)
        );
    }

    /**
     * Extracts variables from the input record to be injected into the welcome template.
     *
     * @param welcomeEmailInput the source welcome data.
     * @return a map containing the "name" variable for personalization.
     */
    private Map<String, String> getTemplateVariables(WelcomeEmailInput welcomeEmailInput) {
        Map<String, String> templateVariables = new HashMap<>();
        templateVariables.put("name", welcomeEmailInput.name());
        return templateVariables;
    }
}