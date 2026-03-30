package com.scanly.common.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * A universal wrapper for all API error responses within the Scanly platform.
 * <p>
 * This record ensures that every failure—whether a 400 Bad Request, 401 Unauthorized,
 * or 500 Internal Server Error—returns a predictable JSON structure. This consistency
 * allows frontend consumers to implement a single, robust error-handling logic.
 * </p>
 * @param <T>       The specific type of error details.
 * @param status    The HTTP status code (e.g., 400, 404, 500).
 * @param timestamp The exact moment the error occurred, used for log correlation.
 * @param errorCode A unique, machine-readable string identifying the error category (e.g., "USER_NOT_FOUND").
 * @param message   A human-readable summary of the error (omitted if null).
 * @param path      The URI of the request that triggered the error.
 * @param details   An optional list of granular failure points (e.g., specific field validation failures).
 */
public record HttpErrorResponse<T>(
        int status,
        Instant timestamp,
        String errorCode,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        String message,
        String path,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<T> details
) {}