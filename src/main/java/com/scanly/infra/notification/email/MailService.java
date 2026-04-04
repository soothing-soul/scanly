package com.scanly.infra.notification.email;

import com.scanly.infra.notification.email.config.MailProperties;
import com.scanly.infra.notification.email.exception.DuplicateEmailBodyBuilderException;
import com.scanly.infra.notification.email.exception.EmailBodyBuilderNotFoundException;
import com.scanly.infra.notification.email.exception.MailSendingFailedException;
import com.scanly.infra.notification.email.exception.MimeMessageGenerationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core service responsible for orchestrating the email dispatching process.
 * <p>
 * This service implements the Strategy Pattern by maintaining a registry of
 * {@link EmailBodyBuilder} implementations. On startup, it automatically
 * discovers all builders in the Spring context and validates that there are
 * no conflicting definitions for a single {@link EmailPurpose}.
 * </p>
 *
 * @author Scanly Infrastructure Team
 * @see EmailBodyBuilder
 * @see EmailRequest
 * @see EmailPurpose
 */
@Component
public class MailService {
    private final MailProperties mailProperties;
    private final JavaMailSender mailSender;
    private final Map<EmailPurpose, EmailBodyBuilder> emailBodyBuilders = new HashMap<>();

    /**
     * Initializes the service by injecting available builders and external mail configurations.
     * <p>
     * During initialization, it populates an internal map for O(1) builder resolution.
     * If multiple builders are found for the same {@link EmailPurpose},
     * a {@link DuplicateEmailBodyBuilderException} is thrown to prevent ambiguous behavior.
     * </p>
     *
     * @param builders       List of all {@link EmailBodyBuilder} beans detected by Spring.
     * @param mailProperties Configuration properties containing the sender's details.
     * @param mailSender     The low-level Spring mail sender implementation.
     * @throws DuplicateEmailBodyBuilderException if two builders attempt to register for the same purpose.
     */
    public MailService(List<EmailBodyBuilder> builders, MailProperties mailProperties, JavaMailSender mailSender) {
        this.mailProperties = mailProperties;
        this.mailSender = mailSender;
        builders.forEach(builder -> {
            if (emailBodyBuilders.putIfAbsent(builder.of(), builder) != null) {
                throw new DuplicateEmailBodyBuilderException(
                        String.format("Multiple EmailBodyBuilder found for %s", builder.of())
                );
            }
        });
    }

    /**
     * Resolves the appropriate builder for a specific email purpose.
     *
     * @param purpose The functional category of the email.
     * @return The associated builder strategy.
     * @throws EmailBodyBuilderNotFoundException if no builder is registered for the purpose.
     */
    private EmailBodyBuilder resolveBuilder(EmailPurpose purpose) {
        EmailBodyBuilder builder = emailBodyBuilders.get(purpose);
        if (builder == null) {
            throw new EmailBodyBuilderNotFoundException(
                    String.format("No EmailBodyBuilder found for purpose: %s" + purpose)
            );
        }
        return builder;
    }

    /**
     * Orchestrates the construction and delivery of an email.
     * <p>
     * This method is generic, allowing it to handle any {@link EmailRequest}
     * regardless of the payload type, provided a corresponding builder exists.
     * </p>
     *
     * @param <T>          The type of the payload.
     * @param emailRequest The data container containing recipient, subject, and payload.
     */
    public <T> void send(EmailRequest<T> emailRequest) {
        MimeMessage mimeMessage = buildMessage(emailRequest);

        try {
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailSendingFailedException(
                    String.format(
                            "Failed to send email to %s for %s",
                            emailRequest.toAddress(),
                            emailRequest.emailPurpose()
                    ),
                    e
            );
        }
    }

    /**
     * Constructs a {@link MimeMessage} by delegating body generation to the correct strategy.
     * <p>
     * It uses a {@link MimeMessageHelper} to ensure the email is sent as UTF-8 HTML.
     * </p>
     *
     * @param <T>          The type of the payload.
     * @param emailRequest The request details.
     * @return A fully prepared MimeMessage ready for transmission.
     * @throws RuntimeException if an error occurs during message assembly.
     */
    private <T> MimeMessage buildMessage(EmailRequest<T> emailRequest) {
        EmailBodyBuilder builder = resolveBuilder(emailRequest.emailPurpose());
        String body = builder.build(emailRequest.payload());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        try {
            helper.setFrom(mailProperties.getFrom());
            helper.setTo(emailRequest.toAddress());
            helper.setSubject(emailRequest.subject());
            helper.setText(body, true); // 'true' indicates HTML content
            return message;
        } catch (MessagingException e) {
            throw new MimeMessageGenerationFailedException("Failed to construct email message", e);
        }
    }
}