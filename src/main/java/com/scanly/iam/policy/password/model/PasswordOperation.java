package com.scanly.iam.policy.password.model;

import com.scanly.iam.policy.password.PasswordPolicy;

/**
 * Categorizes the type of operation requiring password validation.
 * <p>
 * This enum allows {@link PasswordPolicy} implementations to differentiate
 * between various user journeys. For example, a policy might enforce stricter
 * history checks during a {@code CHANGE_PASSWORD} operation than during an
 * initial {@code REGISTRATION}.
 * </p>
 */
public enum PasswordOperation {

    /**
     * Initial account creation.
     * <p>
     * Policies typically focus on baseline complexity and common password
     * blacklists during this phase.
     * </p>
     */
    REGISTRATION,

    /**
     * An authenticated user updating their existing password.
     * <p>
     * Often used to trigger "Password History" checks to ensure the new
     * password hasn't been used recently by the same {@code userId}.
     * </p>
     */
    CHANGE_PASSWORD,

    /**
     * A password update performed via a recovery flow (e.g., "Forgot Password").
     * <p>
     * High-security environments may use this to enforce one-time complexity
     * requirements or to invalidate all existing sessions.
     * </p>
     */
    RESET_PASSWORD
}