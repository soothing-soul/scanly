package com.scanly.infra.notification.email.message;

import com.scanly.infra.notification.email.model.EmailTemplateType;

/**
 * Interface for mapping specific email data inputs into a structured {@link EmailContent} object.
 * <p>
 * This interface follows the Strategy pattern, allowing different implementations to handle
 * specific types of {@link EmailContentInput} (e.g., OTP data, Welcome data). This decouples
 * the raw business data from the formatting logic and template selection.
 * </p>
 */
public interface EmailContentMapper {

    /**
     * Determines if this mapper implementation is capable of processing the provided
     * email content input.
     * <p>
     * This is typically used by a registry or manager to find the correct mapper
     * for a specific domain-specific input class.
     * </p>
     *
     * @param emailContentInput the input data to check.
     * @return {@code true} if this mapper supports the given input; {@code false} otherwise.
     */
    boolean supports(EmailContentInput emailContentInput);

    /**
     * Transforms the raw input data into a structured {@link EmailContent} object.
     * <p>
     * This method is responsible for defining the email subject, selecting the appropriate
     * {@link EmailTemplateType}, and converting the input object's fields into a map of
     * template variables.
     * </p>
     *
     * @param emailContentInput the domain-specific data to be mapped.
     * @return a fully populated {@link EmailContent} ready for the notification request.
     */
    EmailContent map(EmailContentInput emailContentInput);
}