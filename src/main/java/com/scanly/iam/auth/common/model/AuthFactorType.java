package com.scanly.iam.auth.common.model;

/**
 * Categorizes authentication challenges based on the three classic
 * factors of identity verification.
 * <p>
 * In a secure Multi-Factor Authentication (MFA) strategy, "multifactor"
 * implies that the user must provide credentials from different categories
 * (e.g., a password AND a hardware token).
 * </p>
 */
public enum AuthFactorType {

    /**
     * "Something you know."
     * <p>
     * Credentials based on information that the user has memorized.
     * Examples: Passwords, PINs, or answers to security questions.
     * </p>
     */
    KNOWLEDGE,

    /**
     * "Something you have."
     * <p>
     * Credentials based on physical or digital objects in the user's possession.
     * Examples: Hardware security keys (YubiKey), SMS/Email OTPs,
     * or software authenticators (Google Authenticator).
     * </p>
     */
    POSSESSION,

    /**
     * "Something you are."
     * <p>
     * Credentials based on the unique biological characteristics of the user.
     * Examples: Fingerprints, facial recognition, or iris scans.
     * </p>
     */
    INHERITANCE
}