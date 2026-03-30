package com.scanly.iam.policy.password.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized configuration properties for password validation rules.
 * <p>
 * These properties are mapped from the {@code password} prefix in the
 * application's configuration files (e.g., {@code application.yml} or
 * {@code application.properties}).
 * <p>
 * Example configuration:
 * <pre>
 * password:
 * minimum-length: 12
 * maximum-length: 64
 * special-characters-required: true
 * </pre>
 */
@ConfigurationProperties(prefix = "password")
public class PasswordConfigProperties {

    /** Minimum number of characters required for a valid password. */
    private int minimumlength;

    /** Maximum number of characters allowed for a password to prevent buffer issues or DOS. */
    private int maximumLength;

    /** Flag to determine if at least one non-alphanumeric character is mandatory. */
    private boolean specialCharactersRequired;

    /**
     * Gets the configured minimum password length.
     * @return The minimum character count.
     */
    public int getMinimumlength() {
        return minimumlength;
    }

    /**
     * Sets the minimum password length.
     * @param minimumlength The new minimum requirement.
     */
    public void setMinimumlength(int minimumlength) {
        this.minimumlength = minimumlength;
    }

    /**
     * Gets the configured maximum password length.
     * @return The maximum character count.
     */
    public int getMaximumLength() {
        return maximumLength;
    }

    /**
     * Sets the maximum password length.
     * @param maximumLength The new maximum limit.
     */
    public void setMaximumLength(int maximumLength) {
        this.maximumLength = maximumLength;
    }

    /**
     * Checks if special characters are mandated by the current security policy.
     * @return {@code true} if special characters are required; {@code false} otherwise.
     */
    public boolean isSpecialCharactersRequired() {
        return specialCharactersRequired;
    }

    /**
     * Toggles the requirement for special characters.
     * @param specialCharactersRequired The new requirement state.
     */
    public void setSpecialCharactersRequired(boolean specialCharactersRequired) {
        this.specialCharactersRequired = specialCharactersRequired;
    }
}