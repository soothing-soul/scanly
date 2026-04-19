package com.scanly.infra.notification.email.service;

import com.scanly.infra.notification.email.config.MailProperties;
import com.scanly.infra.notification.email.exception.MailSendingFailedException;
import com.scanly.infra.notification.email.exception.MimeMessageGenerationFailedException;
import com.scanly.infra.notification.email.model.EmailRecipient;
import com.scanly.infra.notification.email.model.EmailRequest;
import com.scanly.infra.notification.email.render.EmailBodyRenderer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * The final leg of the email notification journey within the Scanly infrastructure.
 * <p>
 * This service is responsible for the heavy lifting: transforming our high-level
 * {@link EmailRequest} models into standard {@link MimeMessage} objects and
 * physically dispatching them over SMTP via the {@link JavaMailSender}.
 * </p>
 * <p>
 * Think of this as the "Post Office" of the system—it takes the written message,
 * puts it in a standard envelope, and handles the handshake with the mail server.
 * </p>
 */
@Service
public class MailService {

    /**
     * Configuration properties containing global mail settings like the "from" address.
     */
    private final MailProperties mailProperties;

    /**
     * The Spring-abstracted mail sender that manages the actual SMTP connection.
     */
    private final JavaMailSender mailSender;

    /**
     * The rendering engine used to turn template data into final HTML strings.
     */
    private final EmailBodyRenderer emailBodyRenderer;

    /**
     * Constructs the MailService with all required dependencies.
     *
     * @param mailProperties    The global mail configuration.
     * @param mailSender        The low-level transmission engine.
     * @param emailBodyRenderer The component responsible for HTML body generation.
     */
    public MailService(MailProperties mailProperties, JavaMailSender mailSender, EmailBodyRenderer emailBodyRenderer) {
        this.mailProperties = mailProperties;
        this.mailSender = mailSender;
        this.emailBodyRenderer = emailBodyRenderer;
    }

    /**
     * Dispatches an email notification to its destination.
     * <p>
     * This method orchestrates the creation of the message and its transmission.
     * If the mail server is unreachable or rejects the message, it wraps the
     * underlying exception in a domain-specific {@link MailSendingFailedException}.
     * </p>
     *
     * @param emailRequest The request containing recipient details and message content.
     * @throws MailSendingFailedException if the transmission to the mail server fails.
     */
    public void send(EmailRequest emailRequest) {
        MimeMessage mimeMessage = createMimeMessage(emailRequest);

        try {
            // Terminal point: The message leaves our infrastructure here.
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new MailSendingFailedException(
                    String.format(
                            "Failed to send email to %s",
                            emailRequest.getTarget().getRecipient().toAddress()
                    ),
                    e
            );
        }
    }

    /**
     * Assembles a standards-compliant {@link MimeMessage} from our internal models.
     * <p>
     * This method handles the conversion process:
     * 1. Renders the body using the {@link EmailBodyRenderer}.
     * 2. Initializes a {@link MimeMessageHelper} with UTF-8 support.
     * 3. Maps headers (From, To, Subject) from the request and global properties.
     * </p>
     *
     * @param emailRequest The request data to be converted.
     * @return A fully prepared {@link MimeMessage} ready for delivery.
     * @throws MimeMessageGenerationFailedException if header population or MIME structure fails.
     */
    private MimeMessage createMimeMessage(EmailRequest emailRequest) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String emailBody = emailBodyRenderer.build(emailRequest.getMessage());

        // We use UTF-8 to ensure that international characters and emojis survive the journey.
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            EmailRecipient recipient = emailRequest.getTarget().getRecipient();
            helper.setFrom(mailProperties.getFrom());
            helper.setTo(recipient.toAddress());
            helper.setSubject(emailRequest.getMessage().getSubject());

            // Second parameter 'true' indicates the text should be rendered as HTML.
            helper.setText(emailBody, true);

            return mimeMessage;
        } catch (MessagingException e) {
            throw new MimeMessageGenerationFailedException("Failed to construct email message", e);
        }
    }
}