package com.scanly.infra.notification.email.model;

import com.scanly.infra.notification.common.NotificationChannel;
import com.scanly.infra.notification.common.NotificationTarget;

/**
 * Implementation of {@link NotificationTarget} specifically for email communication.
 * <p>
 * This class serves as a concrete routing instruction, pairing an {@link EmailRecipient}
 * with the {@link NotificationChannel#EMAIL} type. Using this class ensures that
 * the notification dispatcher correctly identifies the delivery medium as email
 * and provides the necessary recipient metadata.
 * </p>
 */
public class EmailTarget implements NotificationTarget {

    /**
     * The specific recipient data required for email delivery.
     */
    private final EmailRecipient recipient;

    /**
     * Creates an email target for the provided recipient.
     *
     * @param recipient the {@link EmailRecipient} containing the destination address.
     */
    public EmailTarget(EmailRecipient recipient) {
        this.recipient = recipient;
    }

    /**
     * Returns the fixed notification channel for this target.
     *
     * @return Always returns {@link NotificationChannel#EMAIL}.
     */
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    /**
     * Returns the recipient associated with this email target.
     *
     * @return the {@link EmailRecipient} instance.
     */
    @Override
    public EmailRecipient getRecipient() {
        return recipient;
    }
}