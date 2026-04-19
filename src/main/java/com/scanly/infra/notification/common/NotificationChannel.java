package com.scanly.infra.notification.common;

/**
 * Defines the supported communication mediums for the notification system.
 * <p>
 * This enumeration is used by the dispatcher to route messages to the
 * correct third-party provider (e.g., SMTP for Email, Twilio for SMS).
 * </p>
 */
public enum NotificationChannel {

    /**
     * Standard Electronic Mail.
     * Best for long-form content, HTML templates, and non-urgent records.
     */
    EMAIL,

    /**
     * Short Message Service.
     * Best for urgent, time-sensitive alerts or Two-Factor Authentication (2FA).
     */
    SMS,

    /**
     * WhatsApp Business Platform.
     * Best for rich-media messaging and interactive customer engagement.
     */
    WHATSAPP
}