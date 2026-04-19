package com.scanly.infra.notification.common;

/**
 * Defines the specific routing destination for a notification.
 * <p>
 * This interface acts as a "routing descriptor" by pairing a
 * {@link NotificationRecipient} (the "Who") with a specific
 * {@link NotificationChannel} (the "How").
 * </p>
 * <p>
 * Example: If a single User needs to receive an update via both Email and SMS,
 * the system would create two distinct {@code NotificationTarget} instances
 * for that User.
 * </p>
 * * @author Scanly Infrastructure Team
 */
public interface NotificationTarget {

    /**
     * Returns the specific communication medium for this delivery target.
     * @return The {@link NotificationChannel} to be used.
     */
    NotificationChannel getChannel();

    /**
     * Returns the entity intended to receive the message.
     * @return The {@link NotificationRecipient} associated with this target.
     */
    NotificationRecipient getRecipient();
}