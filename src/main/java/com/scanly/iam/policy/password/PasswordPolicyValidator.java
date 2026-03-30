package com.scanly.iam.policy.password;

import com.scanly.common.exception.ScanlyBaseException;
import com.scanly.iam.policy.exception.PasswordPolicyValidationException;
import com.scanly.iam.policy.model.PolicyValidationResult;
import com.scanly.iam.policy.model.PolicyValidationStatus;
import com.scanly.iam.policy.password.model.PasswordContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Orchestrates the validation of password security requirements.
 * <p>
 * This component aggregates all registered {@link PasswordPolicy} beans and
 * executes them against a given {@link PasswordContext}. It ensures that
 * passwords meet all corporate and security standards (e.g., length,
 * complexity, history) before they are accepted by the system.
 */
@Component
public class PasswordPolicyValidator {

    /** The collection of password rules to be enforced. */
    private final List<PasswordPolicy> passwordPolicies;

    /**
     * Constructs the validator by injecting all available password policies.
     *
     * @param passwordPolicies A list of all beans implementing {@link PasswordPolicy}.
     */
    public PasswordPolicyValidator(List<PasswordPolicy> passwordPolicies) {
        this.passwordPolicies = passwordPolicies;
    }

    /**
     * Validates a password against all active policies.
     * <p>
     * This method performs an exhaustive check, meaning it runs every policy
     * regardless of whether a previous one failed. If any failures occur,
     * they are collected and thrown as a single aggregated exception.
     *
     * @param passwordContext The context containing the password and related metadata.
     * @throws PasswordPolicyValidationException if one or more policies fail.
     */
    public void validate(PasswordContext passwordContext) {
        List<PolicyValidationResult> results = passwordPolicies
                .stream()
                .map(policy -> policy.validate(passwordContext))
                .filter(result -> result.status() == PolicyValidationStatus.FAILURE)
                .toList();

        if (!results.isEmpty()) {
            throw wrapExceptionWithError(results);
        }
    }

    /**
     * Helper method to bundle multiple validation failures into a unified exception.
     * <p>
     * Utilizing the fluent {@code withContext} method from the base exception,
     * this method attaches every specific error code and message to the
     * {@link PasswordPolicyValidationException}, providing the end-user with
     * a complete list of requirements they need to fix.
     * </p>
     *
     * @param results The list of failed validation results.
     * @return A single exception containing the full validation context.
     */
    private PasswordPolicyValidationException wrapExceptionWithError(List<PolicyValidationResult> results) {
        ScanlyBaseException ex =
                new PasswordPolicyValidationException(
                        "Password policy validation failed"
                );

        for (PolicyValidationResult result : results) {
            ex = ex.withContext(
                    result.errorCode(), result.policyError()
            );
        }
        return (PasswordPolicyValidationException) ex;
    }
}
