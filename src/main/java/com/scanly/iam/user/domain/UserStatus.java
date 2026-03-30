package com.scanly.iam.user.domain;

/**
 * Represents the current operational state of a user account.
 * <p>
 * This status determines whether a user is permitted to authenticate,
 * if their data is subject to retention policies, and how they are
 * treated by administrative workflows.
 */
public enum UserStatus {

    /**
     * The account is in good standing and the user has full access
     * to the system according to their assigned roles.
     */
    ACTIVE("ACTIVE"),

    /**
     * The account has been manually disabled by an administrator,
     * typically for policy violations or during investigations.
     * Login is prohibited until explicitly reinstated.
     */
    SUSPENDED("SUSPENDED"),

    /**
     * The account is logically removed from the system.
     * Usually implies that the user's data is flagged for anonymization
     * or permanent deletion according to GDPR/privacy regulations.
     */
    DELETED("DELETED");

    /** The string representation of the status for database or API serialization. */
    private final String value;

    /**
     * Internal constructor for the status enum.
     * @param value The raw string value.
     */
    UserStatus(String value) {
        this.value = value;
    }

    /**
     * Retrieves the string value of the status.
     * @return The status as a string.
     */
    public String getValue() {
        return value;
    }
}