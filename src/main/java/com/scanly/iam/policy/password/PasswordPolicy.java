package com.scanly.iam.policy.password;

import com.scanly.iam.policy.model.PolicyValidationResult;
import com.scanly.iam.policy.password.model.PasswordContext;

/**
 * Strategy interface for defining a single password validation rule.
 * <p>
 * Implementations of this interface are responsible for inspecting a
 * {@link PasswordContext} and determining if the password meets specific
 * security or business criteria (e.g., minimum length, special characters,
 * or check against common passwords).
 * <p>
 * These policies are typically collected and executed by a central
 * {@code PasswordPolicyValidator}.
 */
public interface PasswordPolicy {

    /**
     * Evaluates the password provided in the context against a specific rule.
     *
     * @param context The data containing the password (and potentially metadata
     * like user history) to be validated.
     * @return A {@link PolicyValidationResult} indicating whether the password
     * passed (SUCCESS) or failed (FAILURE) this specific policy.
     */
    PolicyValidationResult validate(PasswordContext context);
}