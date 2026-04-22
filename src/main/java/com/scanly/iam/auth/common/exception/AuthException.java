package com.scanly.iam.auth.common.exception;

import com.scanly.iam.exception.IamException;
import com.scanly.common.exception.ScanlyError;

/**
 * Base abstract exception for all authentication-related errors within the IAM module.
 * <p>
 * This class serves as the root of the authentication exception hierarchy. By extending
 * {@link IamException}, it ensures that all auth failures carry a structured
 * {@link ScanlyError} for consistent API error responses.
 * </p>
 * <p>
 * As an {@code abstract} class, it is intended to be specialized into more descriptive
 * exceptions (e.g., {@code MultiFactorAuthenticationException}) rather than being
 * instantiated directly.
 * </p>
 */
public abstract class AuthException extends IamException {

    /**
     * Constructs a new AuthException with a descriptive message and a structured error.
     *
     * @param message A detailed message explaining the authentication failure.
     * @param error   The {@link ScanlyError} containing the specific error code and
     * metadata for the client.
     */
    protected AuthException(String message, ScanlyError error) {
        super(message, error);
    }

    /**
     * Constructs a new AuthException with a descriptive message, a structured error,
     * and the underlying cause.
     *
     * @param message A detailed message explaining the authentication failure.
     * @param error   The {@link ScanlyError} containing the specific error code and
     * metadata for the client.
     * @param cause   The underlying cause of the exception (e.g., a Database exception
     * or a third-party provider error).
     */
    protected AuthException(String message, ScanlyError error, Throwable cause) {
        super(message, error, cause);
    }
}