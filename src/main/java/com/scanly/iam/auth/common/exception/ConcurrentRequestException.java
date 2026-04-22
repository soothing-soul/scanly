package com.scanly.iam.auth.common.exception;

import com.scanly.iam.auth.common.service.AuthLockCache;

/**
 * Exception thrown when multiple simultaneous authentication requests are detected
 * for the same user or flow.
 * <p>
 * This exception is typically triggered when the {@link AuthLockCache} fails to acquire
 * a distributed lock. It serves as a concurrency guard to prevent race conditions, such as:
 * <ul>
 * <li>Multiple MFA code submissions for the same session.</li>
 * <li>Simultaneous login attempts from different devices/tabs for the same account.</li>
 * <li>State corruption in the underlying session store during rapid clicks.</li>
 * </ul>
 * </p>
 * <p>
 * <b>Handling:</b> Clients receiving this error should typically implement a
 * retry-after delay or notify the user that an operation is already in progress.
 * </p>
 *
 * @see AuthError#CONCURRENT_REQUEST_ERROR
 */
public class ConcurrentRequestException extends AuthException {

    /** The constant error mapping that binds this exception to a concurrency-related status. */
    private static final AuthError error = AuthError.CONCURRENT_REQUEST_ERROR;

    /**
     * Constructs a new ConcurrentRequestException with a custom explanation.
     *
     * @param message A detail message explaining the concurrency conflict.
     */
    public ConcurrentRequestException(String message) {
        super(message, error);
    }
}