package com.scanly.infra.notification.common;

import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Orchestrator service responsible for dispatching notifications to the appropriate delivery providers.
 * <p>
 * This service acts as a centralized dispatcher that implements the Strategy pattern.
 * It maintains a collection of all available {@link NotificationSender} implementations
 * and routes incoming {@link NotificationRequest}s to the sender that supports the
 * requested {@link NotificationChannel}.
 * </p>
 */
@Service
public class NotificationService {

    /**
     * A list of all registered notification senders (e.g., Email, SMS, WhatsApp).
     * These are automatically injected by the Spring container.
     */
    private final List<NotificationSender> senders;

    /**
     * Constructs the service with a list of available sender implementations.
     *
     * @param senders A list of {@link NotificationSender} beans discovered in the application context.
     */
    public NotificationService(List<NotificationSender> senders) {
        this.senders = senders;
    }

    /**
     * Processes a notification request by identifying and invoking the compatible sender.
     * <p>
     * The method retrieves the desired {@link NotificationChannel} from the request target
     * and iterates through the registered senders. The first sender that returns {@code true}
     * for {@link NotificationSender#supportsChannel(NotificationChannel)} is tasked
     * with the delivery.
     * </p>
     *
     * @param request The {@link NotificationRequest} containing the message payload and routing details.
     * @throws IllegalStateException if no sender is found for the requested channel (optional logic to add).
     */
    void send(NotificationRequest request) {
        NotificationChannel channel = request.getTarget().getChannel();

        for (NotificationSender sender : senders) {
            if (sender.supportsChannel(channel)) {
                sender.send(request);
                break;
            }
        }
    }
}