package com.scanly.iam.policy.password.impl;

import com.scanly.iam.policy.model.PolicyValidationResult;
import com.scanly.iam.policy.model.PolicyValidationStatus;
import com.scanly.iam.policy.password.model.PasswordContext;
import com.scanly.iam.policy.password.PasswordPolicy;
import com.scanly.iam.policy.password.config.PasswordConfigProperties;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link PasswordPolicy} that enforces minimum and maximum
 * length requirements.
 * <p>
 * This policy ensures that passwords are long enough to resist brute-force
 * attacks while also being short enough to prevent potential Denial of Service (DoS)
 * vectors related to excessive hashing computations.
 * <p>
 * Thresholds are dynamically fetched from {@link PasswordConfigProperties}.
 */
@Component
public class PasswordLengthPolicy implements PasswordPolicy {

    /** The configuration properties containing length thresholds. */
    private final PasswordConfigProperties passwordProperties;

    /**
     * Constructs the policy with injected configuration properties.
     *
     * @param passwordProperties The centralized password configuration bean.
     */
    public PasswordLengthPolicy(PasswordConfigProperties passwordProperties) {
        this.passwordProperties = passwordProperties;
    }

    /**
     * Validates that the password length falls within the configured range.
     * <p>
     * If the password is shorter than {@code minimumLength} or longer than
     * {@code maximumLength}, a failure result is returned with the error code
     * {@code "PASSWORD_LENGTH_INVALID"}.
     *
     * @param context The context containing the password to be measured.
     * @return A {@link PolicyValidationResult} indicating if the length is acceptable.
     */
    @Override
    public PolicyValidationResult validate(PasswordContext context) {
        String password = context.password();

        // Check if length is outside the bounds defined in application configuration
        if (password.length() < passwordProperties.getMinimumlength()
                || password.length() > passwordProperties.getMaximumLength()
        ) {
            return new PolicyValidationResult(
                    PolicyValidationStatus.FAILURE,
                    "PASSWORD_LENGTH_INVALID",
                    "Password must be between " + passwordProperties.getMinimumlength() +
                            " and " + passwordProperties.getMaximumLength() + " characters."
            );
        } else {
            // Returns a successful result (status=SUCCESS, error=null)
            return new PolicyValidationResult();
        }
    }
}