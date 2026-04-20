package com.scanly.otp.model;

/**
 * Defines the intended use case for a generated One-Time Password (OTP).
 * <p>
 * This enum allows the OTP service to track the context of a challenge
 * without enforcing domain-specific logic.
 */
public enum OtpPurpose {

    /**
     * Used for identity confirmation during the login process or
     * multi-factor authentication (MFA) challenges.
     */
    AUTHENTICATION,

    /**
     * Used to authorize a password change request for an existing account.
     */
    PASSWORD_RESET,

    /**
     * Used for initial validation of user contact information, such as
     * confirming an email address during registration.
     */
    VERIFICATION
}