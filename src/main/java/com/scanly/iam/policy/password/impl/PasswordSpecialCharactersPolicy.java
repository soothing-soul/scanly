package com.scanly.iam.policy.password.impl;

import com.scanly.iam.policy.model.PolicyValidationResult;
import com.scanly.iam.policy.model.PolicyValidationStatus;
import com.scanly.iam.policy.password.PasswordPolicy;
import com.scanly.iam.policy.password.config.PasswordConfigProperties;
import com.scanly.iam.policy.password.model.PasswordContext;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link PasswordPolicy} that enforces the inclusion of special characters.
 * <p>
 * This policy checks if the password contains at least one non-alphanumeric character
 * (e.g., !, @, #, $, etc.). The enforcement is conditional based on the
 * {@code special-characters-required} flag in the application configuration.
 * </p>
 */
@Component
public class PasswordSpecialCharactersPolicy implements PasswordPolicy {

    /** Configuration source for determining if this policy is active. */
    private final PasswordConfigProperties passwordConfigProperties;

    /**
     * Constructs the policy with injected configuration properties.
     *
     * @param passwordConfigProperties The centralized password configuration bean.
     */
    public PasswordSpecialCharactersPolicy(PasswordConfigProperties passwordConfigProperties) {
        this.passwordConfigProperties = passwordConfigProperties;
    }

    /**
     * Validates the presence of special characters in the password.
     * <p>
     * If the requirement is disabled in the configuration, this method returns
     * {@link PolicyValidationStatus#SUCCESS} immediately. Otherwise, it scans
     * the password for at least one special character.
     * </p>
     *
     * @param passwordContext The context containing the password to be inspected.
     * @return A {@link PolicyValidationResult} indicating if the password meets
     * the complexity requirement.
     */
    @Override
    public PolicyValidationResult validate(PasswordContext passwordContext) {
        String password = passwordContext.password();

        // Check if the business rule requires special characters
        if (passwordConfigProperties.isSpecialCharactersRequired()) {
            if (!hasSpecialCharacters(password)) {
                return new PolicyValidationResult(
                        PolicyValidationStatus.FAILURE,
                        "SPECIAL_CHARACTERS_REQUIRED",
                        "The password must contain at least one special character."
                );
            }
        }

        // Return success if requirements are met or if the check is disabled
        return new PolicyValidationResult();
    }

    /**
     * Helper method to determine if a string contains non-alphanumeric ASCII characters.
     * <p>
     * It specifically looks for characters in the printable ASCII range (33-126)
     * that are neither letters nor digits.
     *
     * @param password The password string to check.
     * @return {@code true} if at least one special character is found; {@code false} otherwise.
     */
    private boolean hasSpecialCharacters(String password) {
        return password
                .chars()
                .anyMatch(
                        c -> !Character.isLetterOrDigit(c) && c >= 33  && c <= 126
                );
    }
}