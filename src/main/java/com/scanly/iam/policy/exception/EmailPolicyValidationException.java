package com.scanly.iam.policy.exception;

import com.scanly.iam.exception.IamError;
import com.scanly.iam.exception.IamException;

/**
 * Exception thrown when an email address fails to meet the requirements
 * defined by the registered {@code EmailPolicy} implementations.
 * <p>
 * This exception encapsulates the specific {@link IamError#EMAIL_POLICY_VALIDATION_FAILURE}
 * error code, allowing global exception handlers to identify and format policy
 * violations consistently across the IAM module.
 * <p>
 * Use the {@code withContext} method (inherited from {@code ScanlyBaseException})
 * to attach multiple validation failure details to this exception.
 */
public class EmailPolicyValidationException extends IamException {

    /** The standardized IAM error associated with email policy failures. */
    private static final IamError error = IamError.EMAIL_POLICY_VALIDATION_FAILURE;

    /**
     * Constructs a new exception with a specific detail message.
     *
     * @param message A descriptive message explaining the validation failure.
     */
    public EmailPolicyValidationException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a specific detail message and a root cause.
     *
     * @param message A descriptive message explaining the validation failure.
     * @param cause   The underlying cause of the exception.
     */
    public EmailPolicyValidationException(String message, Throwable cause) {
        super(message, error, cause);
    }
}