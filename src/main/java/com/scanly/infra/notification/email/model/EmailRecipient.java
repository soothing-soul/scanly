package com.scanly.infra.notification.email.model;

import com.scanly.infra.notification.common.NotificationRecipient;
import com.scanly.infra.notification.common.NotificationSender;

/**
 * An email-specific implementation of a {@link NotificationRecipient}.
 * <p>
 * This record encapsulates the minimum addressing information required to deliver
 * a message via the {@code EMAIL} channel. It is used by email-based
 * {@link NotificationSender} implementations
 * to populate the "To" field of an outgoing SMTP or API-based email.
 * </p>
 * @param toAddress The destination email address for the recipient (e.g., "support@scanly.com").
 * @author Scanly Infrastructure Team
 */
public record EmailRecipient(
        String toAddress
) implements NotificationRecipient {}