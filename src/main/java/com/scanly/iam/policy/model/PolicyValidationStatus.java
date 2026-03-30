package com.scanly.iam.policy.model;

/**
 * Defines the possible outcomes of a single policy validation check.
 * <p>
 * This status is used by {@link PolicyValidationResult}
 * to indicate whether a specific security or business rule was satisfied.
 */
public enum PolicyValidationStatus {

    /**
     * Indicates that the input (email, password, etc.) successfully
     * met all the requirements of the evaluated policy.
     */
    SUCCESS,

    /**
     * Indicates that the input failed to meet one or more requirements
     * of the evaluated policy.
     */
    FAILURE
}
