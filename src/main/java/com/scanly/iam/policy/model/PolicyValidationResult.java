package com.scanly.iam.policy.model;

/**
 * Encapsulates the outcome of a single policy evaluation.
 * <p>
 * This record combines a {@link PolicyValidationStatus} with detailed error information
 * in the event of a failure. It is designed to be returned by implementations of
 * {@code EmailPolicy} or {@code PasswordPolicy}.
 * @param status      The result of the validation (SUCCESS or FAILURE).
 * @param policyError The specific error details; typically {@code null} if the status is SUCCESS.
 */
public record PolicyValidationResult(
        PolicyValidationStatus status,
        PolicyValidationError policyError
) {
    /**
     * Default constructor representing a successful validation.
     * <p>
     * Initializes the result with {@link PolicyValidationStatus#SUCCESS} and
     * a {@code null} error object.
     */
    public PolicyValidationResult () {
        this(PolicyValidationStatus.SUCCESS, null);
    }

    /**
     * Convenience constructor for creating a failure result with inline error details.
     *
     * @param status    The validation status (usually {@link PolicyValidationStatus#FAILURE}).
     * @param errorCode A unique string identifier for the specific policy violation.
     * @param message   A descriptive message explaining the violation.
     */
    public PolicyValidationResult (PolicyValidationStatus status, String errorCode, String message) {
        this(status, new PolicyValidationError(errorCode, message));
    }

    /**
     * Helper method to retrieve the error code from the underlying error object.
     * * @return The error code string, or {@code null} if no error exists.
     */
    public String errorCode() {
        return policyError != null ? policyError.errorCode() : null;
    }

    /**
     * Helper method to retrieve the error message from the underlying error object.
     * * @return The error message string, or {@code null} if no error exists.
     */
    public String message() {
        return policyError != null ? policyError.message() : null;
    }
}