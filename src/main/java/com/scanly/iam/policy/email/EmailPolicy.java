package com.scanly.iam.policy.email;

import com.scanly.iam.policy.email.model.EmailContext;
import com.scanly.iam.policy.model.PolicyValidationResult;

/**
 * A functional interface for defining business rules regarding email addresses.
 * <p>
 * Implementations should focus on a single responsibility (e.g., domain validation,
 * disposable email detection, or format restrictions) to maintain high cohesion
 * and low coupling within the IAM module.
 * </p>
 */
@FunctionalInterface
public interface EmailPolicy {

    /**
     * Validates an email address based on the provided context.
     *
     * @param emailContext Data containing the email address and any additional
     * metadata required for the policy check.
     * @return A {@link PolicyValidationResult} indicating whether the email
     * satisfies the policy, along with error details if it fails.
     */
    PolicyValidationResult validate(EmailContext emailContext);
}