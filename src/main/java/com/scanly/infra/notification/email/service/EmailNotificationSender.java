package com.scanly.infra.notification.email.service;

import com.scanly.infra.notification.common.NotificationChannel;
import com.scanly.infra.notification.common.NotificationRequest;
import com.scanly.infra.notification.common.NotificationSender;
import com.scanly.infra.notification.email.exception.InvalidEmailRequestException;
import com.scanly.infra.notification.email.model.EmailRequest;

/**
 * An implementation of {@link NotificationSender} dedicated to handling email communications.
 * <p>
 * This class acts as a specialized adapter within the notification infrastructure.
 * It filters for requests intended for the {@link NotificationChannel#EMAIL} channel
 * and delegates the actual transmission logic to the underlying {@link MailService}.
 * </p>
 */
public class EmailNotificationSender implements NotificationSender {

    /**
     * The low-level mail service used to interface with SMTP servers or email APIs.
     */
    private final MailService mailService;

    /**
     * Constructs the sender with the required mail service dependency.
     *
     * @param mailService the service responsible for the physical delivery of emails.
     */
    public EmailNotificationSender(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * Identifies if this sender supports the provided communication channel.
     *
     * @param channel the {@link NotificationChannel} to check.
     * @return {@code true} if the channel is {@link NotificationChannel#EMAIL}; {@code false} otherwise.
     */
    @Override
    public boolean supportsChannel(NotificationChannel channel) {
        return NotificationChannel.EMAIL == channel;
    }

    /**
     * Processes the notification request if it is a valid {@link EmailRequest}.
     * <p>
     * This method performs a type check to ensure the request is email-compatible.
     * If valid, it casts the generic {@link NotificationRequest} to an {@link EmailRequest}
     * and triggers the delivery via the {@code mailService}.
     * </p>
     *
     * @param request the notification request containing email-specific payload and target.
     */
    @Override
    public void send(NotificationRequest request) {
        if (!(request instanceof EmailRequest)) {
            throw new InvalidEmailRequestException("Invalid email request");
        }

        EmailRequest emailRequest = (EmailRequest) request;
        mailService.send(emailRequest);
    }
}