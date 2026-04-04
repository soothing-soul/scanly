package com.scanly.infra.notification.email;

/**
 * Strategy interface for constructing the body of an email.
 * <p>
 * Implementations of this interface are responsible for the "specialization"
 * phase of the email lifecycle—transforming a raw data payload into a
 * formatted String (usually HTML) suitable for transmission.
 * </p>
 * <p>
 * New email types can be added to the system by creating a new implementation
 * of this interface and registering it as a Spring {@code @Component}.
 * </p>
 *
 * @param <T> The specific type of the data payload required to build the email body.
 * @see EmailPurpose
 * @see EmailRequest
 */
public interface EmailBodyBuilder<T> {

    /**
     * Returns the specific {@link EmailPurpose} that this builder is capable of handling.
     * <p>
     * This is used by the orchestration layer to route {@link EmailRequest}s
     * to the correct builder implementation.
     * </p>
     *
     * @return The associated email purpose.
     */
    EmailPurpose of();

    /**
     * Constructs the final string representation of the email body.
     * <p>
     * This method typically involves injecting the {@code payload} into an
     * HTML template using a templating engine or string manipulation.
     * </p>
     *
     * @param payload The data object containing the variables needed for the template.
     * @return A {@code String} containing the fully rendered email body.
     */
    String build(T payload);
}