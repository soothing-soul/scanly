package com.scanly.infra.notification.common;

/**
 * The core contract for notification delivery providers within the Scanly infrastructure.
 * <p>
 * Implementations of this interface (e.g., {@code EmailSender}, {@code SmsSender}) are
 * responsible for the low-level integration with third-party APIs or internal
 * protocols to physically transmit the message defined in a {@link NotificationRequest}.
 * </p>
 * <p>
 * This interface allows the notification system to remain provider-agnostic,
 * enabling easy swapping or addition of new delivery vendors.
 * </p>
 */
public interface NotificationSender {

    /**
     * Determines if this sender implementation is capable of handling the specified
     * notification channel.
     * <p>
     * This capability check allows a dispatcher to dynamically route a request to
     * the correct sender based on the {@link NotificationChannel} defined in the target.
     * </p>
     *
     * @param channel The {@link NotificationChannel} to verify (e.g., EMAIL, SMS).
     * @return {@code true} if this sender can process messages for the given channel;
     * {@code false} otherwise.
     */
    boolean supportsChannel(NotificationChannel channel);

    /**
     * Executes the delivery of the notification request.
     * <p>
     * This method is responsible for extracting the message content and recipient
     * details from the {@link NotificationRequest}, performing any channel-specific
     * transformations, and initiating the final transmission to the end user.
     * </p>
     *
     * @param request The {@link NotificationRequest} containing the payload and
     * routing information.
     */
    void send(NotificationRequest request);
}