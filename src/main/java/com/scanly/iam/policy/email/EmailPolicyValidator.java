package com.scanly.iam.policy.email;

import com.scanly.common.exception.ScanlyBaseException;
import com.scanly.iam.policy.email.model.EmailContext;
import com.scanly.iam.policy.exception.EmailPolicyValidationException;
import com.scanly.iam.policy.model.PolicyValidationResult;
import com.scanly.iam.policy.model.PolicyValidationStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Orchestrates the validation of email-related operations against a set of defined policies.
 * <p>
 * This component automatically discovers all Spring-managed beans implementing the
 * {@link EmailPolicy} interface and executes them in sequence. It serves as a
 * single entry point for verifying if an email context satisfies all business and
 * security requirements.
 * </p>
 */
@Component
public class EmailPolicyValidator {

    /** Collection of all registered email policies. */
    private final List<EmailPolicy> emailPolicies;

    /**
     * Constructs the validator with a list of available policies.
     * Spring automatically injects all implementations of {@link EmailPolicy} found in the context.
     *
     * @param emailPolicies The list of policies to be evaluated during validation.
     */
    public EmailPolicyValidator(List<EmailPolicy> emailPolicies) {
        this.emailPolicies = emailPolicies;
    }

    /**
     * Validates the provided {@link EmailContext} against all registered policies.
     * <p>
     * This method evaluates every policy. If one or more policies return a
     * {@link PolicyValidationStatus#FAILURE}, the errors are aggregated into a single
     * exception which is then thrown.
     *
     * @param emailContext The data and metadata of the email being validated.
     * @throws EmailPolicyValidationException if any policy validation fails.
     */
    public void validate(EmailContext emailContext) {
        List<PolicyValidationResult> results = emailPolicies
                .stream()
                .map(policy -> policy.validate(emailContext))
                .filter(result -> result.status() == PolicyValidationStatus.FAILURE)
                .toList();

        if (!results.isEmpty()) {
            throw wrapExceptionWithError(results);
        }
    }

    /**
     * Aggregates multiple validation failures into a single exception.
     * <p>
     * This helper method iterates through the failed results and attaches their
     * specific error codes and messages to the exception context, allowing for
     * detailed error reporting to the caller.
     *
     * @param results A list of failed {@link PolicyValidationResult} objects.
     * @return A configured {@link EmailPolicyValidationException} containing all error details.
     */
    private EmailPolicyValidationException wrapExceptionWithError(List<PolicyValidationResult> results) {
        ScanlyBaseException ex =
                new EmailPolicyValidationException(
                        "Email policy validation failed"
                );

        for (PolicyValidationResult result : results) {
            ex = ex.withContext(
                    result.errorCode(), result.policyError()
            );
        }
        return (EmailPolicyValidationException) ex;
    }
}
