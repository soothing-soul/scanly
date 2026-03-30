package com.scanly.iam.policy.exception;

import com.scanly.iam.exception.IamError;
import com.scanly.iam.exception.IamException;

/**
 * Exception thrown when a password fails to meet the security criteria
 * defined by the active {@code PasswordPolicy} implementations.
 * <p>
 * This exception is specifically mapped to {@link IamError#PASSWORD_POLICY_VALIDATION_FAILURE},
 * ensuring that the API layer returns a consistent error code for issues such as
 * insufficient complexity, inclusion in common password lists, or length requirements.
 * <p>
 * Like other policy exceptions in the Scanly IAM module, it supports error
 * aggregation via the {@code withContext} method to provide the user with a
 * complete list of password requirements they failed to meet.
 */
public class PasswordPolicyValidationException extends IamException {

    /** The standardized IAM error associated with password policy failures. */
    private static final IamError error = IamError.PASSWORD_POLICY_VALIDATION_FAILURE;

    /**
     * Constructs a new exception with a specific detail message.
     *
     * @param message A descriptive message explaining why the password was rejected.
     */
    public PasswordPolicyValidationException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a specific detail message and a root cause.
     *
     * @param message A descriptive message explaining why the password was rejected.
     * @param cause   The underlying cause (e.g., an external validator service error).
     */
    public PasswordPolicyValidationException(String message, Throwable cause) {
        super(message, error, cause);
    }
}