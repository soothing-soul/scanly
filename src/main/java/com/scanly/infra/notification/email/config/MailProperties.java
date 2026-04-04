package com.scanly.infra.notification.email.config;

import com.scanly.infra.notification.email.MailService;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Scanly Email Infrastructure.
 * <p>
 * This class captures settings prefixed with {@code mail} from the application's
 * external configuration sources (e.g., {@code application.yml}). It provides
 * a type-safe way to access global mail settings such as the sender's address.
 * </p>
 *
 * @see MailService
 */
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    /**
     * The email address used as the sender (From) for all outgoing communications.
     * <p>
     * Must be a valid email format (e.g., "support@scanly.com"). Depending on
     * the SMTP provider, this address may need to be pre-verified.
     * </p>
     */
    private String from;

    /**
     * Retrieves the configured sender email address.
     *
     * @return The "From" email address.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the sender email address.
     * <p>
     * This method is called by Spring's configuration binding mechanism
     * during the application context refresh.
     * </p>
     *
     * @param from The email address to be used as the sender.
     */
    public void setFrom(String from) {
        this.from = from;
    }
}