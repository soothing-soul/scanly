package com.scanly.infra.notification.email.model;

import com.scanly.infra.notification.common.NotificationChannel;
import com.scanly.infra.notification.common.NotificationMessage;
import com.scanly.infra.notification.common.NotificationRequest;
import com.scanly.infra.notification.common.NotificationTarget;
import com.scanly.infra.notification.email.message.EmailMessage;

/**
 * A concrete implementation of {@link NotificationRequest} tailored for email delivery.
 * <p>
 * This class encapsulates the specialized {@link EmailTarget} and {@link EmailMessage}
 * required to fulfill an email notification. It provides a structured way to pass
 * email data through the generic notification infrastructure.
 * </p>
 */
public class EmailRequest implements NotificationRequest {

    /**
     * The email-specific routing information, including the recipient and channel.
     */
    private final EmailTarget emailTarget;

    /**
     * The email-specific content, including subject line, body, and potential attachments.
     */
    private final EmailMessage message;

    /**
     * Constructs a new EmailRequest with a predefined target and message.
     *
     * @param emailTarget the destination details for the email.
     * @param message     the content to be sent.
     */
    public EmailRequest(EmailTarget emailTarget, EmailMessage message) {
        this.emailTarget = emailTarget;
        this.message = message;
    }

    /**
     * Retrieves the email message content.
     *
     * @return the {@link EmailMessage} instance associated with this request.
     */
    @Override
    public EmailMessage getMessage() {
        return message;
    }

    /**
     * Retrieves the email target routing information.
     *
     * @return the {@link EmailTarget} instance associated with this request.
     */
    @Override
    public EmailTarget getTarget() {
        return emailTarget;
    }

    /**
     * A static factory method to simplify the creation of an email request from
     * a raw email address and message object.
     * <p>
     * This method handles the internal instantiation of {@link EmailRecipient}
     * and {@link EmailTarget}, reducing boilerplate code for the caller.
     * </p>
     *
     * @param emailAddress the raw string email address of the recipient.
     * @param message      the {@link EmailMessage} content to be delivered.
     * @return a fully initialized {@code EmailRequest} instance.
     */
    public static EmailRequest from(String emailAddress, EmailMessage message) {
        EmailRecipient recipient = new EmailRecipient(emailAddress);
        EmailTarget emailTarget = new EmailTarget(recipient);
        return new EmailRequest(emailTarget, message);
    }
}