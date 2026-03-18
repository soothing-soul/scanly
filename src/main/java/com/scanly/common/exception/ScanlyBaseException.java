package com.scanly.common.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * The foundational exception class for the Scanly platform.
 * <p>
 * This abstract class serves as the root of the application's exception hierarchy.
 * By forcing all custom exceptions to extend this base, the platform ensures:
 * <ul>
 * <li><b>Consistency:</b> Every error carries a standardized {@link ScanlyError} code.</li>
 * <li><b>Traceability:</b> Every error is timestamped at the moment of creation.</li>
 * <li><b>Contextual Richness:</b> Developers can attach arbitrary metadata via the context map.</li>
 * </ul>
 * </p>
 * This exception is designed to be caught by a global {@code @ControllerAdvice},
 * which can then translate the error code and context into a standardized JSON response.
 */
public abstract class ScanlyBaseException extends RuntimeException {

    /**
     * The specific error definition containing the machine-readable code
     * and the intended HTTP status.
     */
    private final ScanlyError error;

    /**
     * A flexible metadata container. Use this to store dynamic information
     * (e.g., 'requestedId', 'reason') that helps in debugging or frontend rendering.
     */
    private final Map<String, Object> context;

    /**
     * The UTC timestamp of when the exception occurred.
     */
    private final Instant timestamp;

    /**
     * Constructs a base exception with a clear message and an associated error category.
     *
     * @param message The developer-friendly description of the failure.
     * @param error   The enum or implementation defining the error code and status.
     */
    protected ScanlyBaseException(String message, ScanlyError error) {
        super(message);
        this.error = error;
        this.timestamp = Instant.now();
        this.context = new HashMap<>();
    }

    /**
     * Constructs a base exception when wrapping a lower-level cause.
     *
     * @param message The developer-friendly description.
     * @param error   The associated error category.
     * @param cause   The underlying {@link Throwable} that triggered this exception.
     */
    protected ScanlyBaseException(String message, ScanlyError error, Throwable cause) {
        super(message, cause);
        this.error = error;
        this.timestamp = Instant.now();
        this.context = new HashMap<>();
    }

    /**
     * Retrieves the machine-readable error code defined by the underlying error.
     * @return The error code string (e.g., "JWT_ERROR_SIGNING_FAILURE").
     */
    public String getErrorCode() {
        return error.getErrorCode();
    }

    /**
     * Retrieves the HTTP status code intended for this exception.
     * @return The {@link HttpStatus} code.
     */
    public HttpStatus getHttpStatus() {
        return error.getHttpStatus();
    }

    /**
     * Returns the metadata context associated with this exception.
     * @return A map of additional failure details.
     */
    public Map<String, Object> getContext() {
        return context;
    }

    /**
     * A fluent-API method to add contextual metadata to the exception.
     * <p>
     * Example: {@code throw new JwtException(...).withContext("token_id", jti);}
     * </p>
     * @param key   The metadata key.
     * @param value The metadata value.
     * @return The current exception instance (for method chaining).
     */
    public ScanlyBaseException withContext(String key, Object value) {
        context.put(key, value);
        return this;
    }

    /**
     * @return The {@link Instant} representing when this exception was instantiated.
     */
    public Instant getTimestamp() {
        return timestamp;
    }
}
