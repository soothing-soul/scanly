package com.scanly.infra.notification.email.message.otp;

import com.scanly.infra.notification.email.message.EmailContent;
import com.scanly.infra.notification.email.message.EmailContentInput;
import com.scanly.infra.notification.email.message.EmailContentMapper;
import com.scanly.infra.notification.email.model.EmailTemplateType;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation of {@link EmailContentMapper} for One-Time Password (OTP) notifications.
 * <p>
 * This component is responsible for translating {@link OtpEmailInput} into a structured
 * {@link EmailContent} object. It ensures the {@link EmailTemplateType#OTP} is selected,
 * and converts raw duration data into human-readable minutes for the email template.
 * </p>
 */
@Component
public class OtpEmailContentMapper implements EmailContentMapper {

    /**
     * Checks if the provided input is an instance of {@link OtpEmailInput}.
     *
     * @param emailContentInput the input data to evaluate.
     * @return {@code true} if the input is an {@link OtpEmailInput}; {@code false} otherwise.
     */
    @Override
    public boolean supports(EmailContentInput emailContentInput) {
        return emailContentInput instanceof OtpEmailInput;
    }

    /**
     * Maps the OTP-specific input into generic email content.
     * <p>
     * This method performs the type cast and coordinates the creation of the
     * template variable map.
     * </p>
     *
     * @param emailContentInput the OTP input data.
     * @return a fully populated {@link EmailContent} instance.
     */
    @Override
    public EmailContent map(EmailContentInput emailContentInput) {
        OtpEmailInput otpEmailInput = (OtpEmailInput) emailContentInput;

        return new EmailContent(
                EmailTemplateType.OTP,
                getTemplateVariables(otpEmailInput)
        );
    }

    /**
     * Populates the template variables map with data required by the OTP HTML/text template.
     *
     * @param otpEmailInput the source OTP data.
     * @return a map containing keys "otp", "expiry", and "purpose".
     */
    private Map<String, String> getTemplateVariables(OtpEmailInput otpEmailInput) {
        Map<String, String> templateVariables = new HashMap<>();
        templateVariables.put("otp", otpEmailInput.otp());
        templateVariables.put("expiry", getExpiryInMinutes(otpEmailInput.expiry()).toString());
        templateVariables.put("purpose", otpEmailInput.purpose().toString());
        return templateVariables;
    }

    /**
     * Converts a {@link Duration} into a total minute count for template display.
     *
     * @param expiry the duration until the OTP expires.
     * @return the number of full minutes in the duration.
     */
    private Long getExpiryInMinutes(Duration expiry) {
        long expiryInSeconds = expiry.toSeconds();
        return expiryInSeconds / 60;
    }
}