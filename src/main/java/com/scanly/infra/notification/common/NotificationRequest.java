package com.scanly.infra.notification.common;

/**
 * Represents a complete request to dispatch a notification within the Scanly infrastructure.
 * <p>
 * This interface serves as the primary data container for the notification engine.
 * It encapsulates both the content of the communication ({@link NotificationMessage})
 * and the specific routing details ({@link NotificationTarget}).
 * </p>
 * <p>
 * A {@code NotificationRequest} is typically passed to a dispatcher or service layer
 * which then interprets the target's channel and recipient to execute the delivery.
 * </p>
 */
public interface NotificationRequest {

    /**
     * Retrieves the content payload associated with this request.
     * @return the {@link NotificationMessage} containing the body, subject, or
     * media to be delivered.
     */
    NotificationMessage getMessage();

    /**
     * Retrieves the target destination and medium for this request.
     * @return the {@link NotificationTarget} which defines who receives the
     * notification and through which {@link NotificationChannel}.
     */
    NotificationTarget getTarget();
}