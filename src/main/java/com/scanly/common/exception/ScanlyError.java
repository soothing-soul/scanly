package com.scanly.common.exception;

import org.springframework.http.HttpStatus;

/**
 * A contract defining the essential components of a platform-standard error.
 * <p>
 * This interface allows feature-specific error enums (e.g., {@code JwtError}, {@code UserError})
 * to be treated uniformly by the core exception handling infrastructure.
 * By implementing this interface, an error definition provides both a machine-readable
 * string code and its corresponding HTTP semantics.
 * </p>
 *
 * By having this interface, we can achieve the following through ENUM
 * 1. HttpStatus lives closer to the actual error code
 * 2. Translation from the error code to HttpStatus is easy
 * 3. Exception doesn't have to worry about these details
 */
public interface ScanlyError {

    /**
     * Retrieves the unique, machine-readable identifier for the error.
     * <p>
     * Conventionally, this should be prefixed by the feature area
     * (e.g., "JWT_ERROR_...", "AUTH_ERROR_...").
     * </p>
     * @return The unique error code string.
     */
    String getErrorCode();

    /**
     * Retrieves the {@link HttpStatus} that most accurately represents
     * this error in a RESTful context.
     * <p>
     * This allows the global exception handler to automatically determine
     * the correct response status without explicit branching logic.
     * </p>
     * @return The intended HTTP status code.
     */
    HttpStatus getHttpStatus();
}