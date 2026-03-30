package com.scanly.iam.registration.exception;

import com.scanly.iam.exception.IamError;
import com.scanly.iam.exception.IamException;

/**
 * Thrown by the Registration Use Case when the enrollment process fails due to
 * internal business logic constraints or downstream service failures.
 * <p>
 * Unlike field-specific exceptions (e.g., {@code EmailAlreadyExistsException}),
 * this exception indicates a broader failure of the registration orchestration.
 * </p>
 * <b>Associated Error:</b> {@link IamError#REGISTRATION_FAILURE}
 * @see IamException
 * @see IamError
 */
public class RegistrationFailedException extends IamException {

    /**
     * The static error mapping for this exception, typically resulting
     * in an HTTP 500 or 422 depending on the IamError definition.
     */
    private static final IamError error = IamError.REGISTRATION_FAILURE;

    /**
     * Constructs a new exception with a specific detail message.
     * @param message Explains the specific cause of the registration failure.
     */
    public RegistrationFailedException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a detail message and an underlying cause.
     * @param message Explains the context of the failure.
     * @param cause   The root cause (e.g., a database timeout or a failed
     * call to an external Identity Provider).
     */
    public RegistrationFailedException(String message, Throwable cause) {
        super(message, error, cause);
    }
}