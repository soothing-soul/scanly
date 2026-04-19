package com.scanly.infra.notification.email.model;

/**
 * Enumeration of available email message templates within the Scanly notification system.
 * <p>
 * This enum maps high-level business events (like user registration or password resets)
 * to their corresponding physical template files (typically stored as Thymeleaf or
 * FreeMarker templates in the resources directory).
 * </p>
 */
public enum EmailTemplateType {

    /**
     * Template for One-Time Password (OTP) delivery.
     * Used for multi-factor authentication and identity verification.
     */
    OTP("otp-email"),

    /**
     * Template for newly registered users.
     * Used to introduce users to the platform after account creation.
     */
    WELCOME("welcome"),

    /**
     * Template for the password recovery process.
     * Contains the secure link or token required to reset a user's credentials.
     */
    RESET_PASSWORD("reset-password");

    /**
     * The base name of the template file (excluding the file extension).
     */
    private final String templateFileName;

    /**
     * Constructs an EmailTemplateType with its associated file name.
     *
     * @param fileName the name of the template file as it exists in the filesystem.
     */
    EmailTemplateType(String fileName) {
        this.templateFileName = fileName;
    }

    /**
     * Retrieves the file name associated with this template type.
     * <p>
     * This name is used by the template engine to locate and render the
     * specific HTML or plaintext content for the email.
     * </p>
     *
     * @return the template file name string.
     */
    public String getTemplateFileName() {
        return templateFileName;
    }
}