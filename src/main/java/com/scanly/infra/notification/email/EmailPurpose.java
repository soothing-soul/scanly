package com.scanly.infra.notification.email;

/**
 * Defines the specific purpose of an outgoing email within the Scanly platform.
 * <p>
 * This enum acts as a strategy discriminator. It is used by the {@code MailService}
 * to look up the appropriate {@code EmailBodyBuilder} implementation required to
 * process the email's HTML template and payload.
 * </p>
 * @see EmailBodyBuilder
 * @see EmailRequest
 */
public enum EmailPurpose {
    /**
     * Used for One-Time Password delivery.
     * <p>
     * Covers scenarios such as multi-factor authentication (MFA),
     * login verification, and email ownership confirmation.
     * </p>
     */
    OTP,

    /**
     * Sent to new users immediately after a successful registration.
     * <p>
     * Typically contains onboarding information and a call-to-action
     * to explore the QR analytics dashboard.
     * </p>
     */
    WELCOME,

    /**
     * Triggered during the "Forgot Password" workflow.
     * <p>
     * Contains the secure link or instructions necessary for a user
     * to update their credentials.
     * </p>
     */
    RESET_PASSWORD,

    /**
     * General category for system-generated security or status notifications.
     * <p>
     * Examples include "New Login Detected," "Account Plan Limit Reached,"
     * or "Suspicious Activity Detected."
     * </p>
     */
    ALERTS
}