package com.scanly.iam.policy.password.model;

import com.scanly.iam.policy.password.PasswordPolicy;

import java.util.UUID;

/**
 * Encapsulates the full context required to validate a password against security policies.
 * <p>
 * Beyond the raw password string, this record provides metadata such as the user's
 * identifier and email address. This allows {@link PasswordPolicy} implementations
 * to enforce advanced rules, such as preventing passwords that contain the user's
 * identity or checking password history for existing users.
 * </p>
 *
 * @param password  The plain-text password candidate currently being validated.
 * @param email     The email address associated with the account, used to prevent
 * identity-based password patterns.
 * @param userId    The unique identifier of the user; may be {@code null} during
 * initial registration/sign-up.
 * @param operation The type of action triggering this validation (e.g., RESET,
 * REGISTRATION, or UPDATE).
 */
public record PasswordContext(
        String password,
        String email,
        UUID userId,
        PasswordOperation operation
) {}
