package com.scanly.infra.notification.email.purpose.otp;

import com.scanly.infra.notification.email.EmailBodyBuilder;
import com.scanly.infra.notification.email.EmailPurpose;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;

/**
 * Concrete implementation of {@link EmailBodyBuilder} for One-Time Password (OTP) emails.
 * <p>
 * This class uses the Thymeleaf {@link TemplateEngine} to render an HTML body
 * based on the {@code otp-email} template. It acts as the specialized strategy
 * for the {@link EmailPurpose#OTP} category.
 * </p>
 *
 * @see EmailBodyBuilder
 * @see OtpPayload
 */
@Component
public class OtpEmailBodyBuilder implements EmailBodyBuilder<OtpPayload> {

    private final TemplateEngine templateEngine;

    /**
     * Constructs the builder with the required Thymeleaf template engine.
     *
     * @param templateEngine The engine used to process HTML templates.
     */
    public OtpEmailBodyBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * @return {@link EmailPurpose#OTP}
     */
    @Override
    public EmailPurpose of() {
        return EmailPurpose.OTP;
    }

    /**
     * Processes the {@link OtpPayload} and renders the OTP email HTML.
     * <p>
     * Variables provided to the template include:
     * <ul>
     * <li>{@code otp}: The verification code.</li>
     * <li>{@code purpose}: The context/reason for the OTP.</li>
     * <li>{@code expiry}: The expiration time expressed in minutes.</li>
     * </ul>
     * </p>
     *
     * @param payload The data container for the OTP details.
     * @return The fully rendered HTML string for the email body.
     */
    @Override
    public String build(OtpPayload payload) {
        Context context = new Context();
        context.setVariable("otp", payload.otp());
        context.setVariable("purpose", payload.purpose());
        context.setVariable("expiry", getMinutes(payload.expiry()));

        return templateEngine.process("otp-email", context);
    }

    /**
     * Converts a {@link Duration} into a total minute count.
     *
     * @param duration The duration to convert.
     * @return The number of full minutes in the duration.
     */
    private long getMinutes(Duration duration) {
        long seconds = duration.getSeconds();
        return seconds / 60;
    }
}