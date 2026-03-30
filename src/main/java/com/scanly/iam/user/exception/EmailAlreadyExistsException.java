package com.scanly.iam.user.exception;

import com.scanly.iam.exception.IamError;
import com.scanly.iam.exception.IamException;
import com.scanly.iam.user.domain.User;

/**
 * Exception thrown when an operation fails because the provided email address
 * is already associated with an existing account.
 * <p>
 * This exception is typically triggered during user registration or when a user
 * attempts to update their profile with an email that is already in use.
 * </p>
 * <b>Mapping:</b> By default, this maps to {@link IamError#EMAIL_ALREADY_EXISTS},
 * which usually results in an {@code HTTP 409 Conflict} status code.
 *
 * <b>Security Note: </b> This exception must be intercepted by the caller service
 * to ensure that the {@link User} <b>MUST NOT</b> know that the given email already
 * exists in the system.
 * @see IamException
 * @see IamError
 */
public class EmailAlreadyExistsException extends IamException {

    private static final IamError error = IamError.EMAIL_ALREADY_EXISTS;

    /**
     * Constructs a new exception with a specific error message.
     * @param message Detailed description of the failure.
     */
    public EmailAlreadyExistsException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new exception with a specific error message and a root cause.
     * @param message Detailed description of the failure.
     * @param cause   The underlying cause (e.g., a Database constraint violation exception).
     */
    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, error, cause);
    }
}