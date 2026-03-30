package com.scanly.common.web.exception;

import com.scanly.common.exception.ScanlyBaseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

/**
 * Centralized exception handling component for all REST controllers.
 * <p>
 * This class intercepts exceptions thrown across the application and transforms
 * them into standardized {@link HttpErrorResponse} objects. This ensures a
 * uniform API contract for frontend consumers and internal logging systems.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles known business and domain-specific exceptions.
     * <p>
     * Intercepts any exception extending {@link ScanlyBaseException}. It uses the
     * metadata provided by the exception (HTTP Status, Error Code, etc.) to
     * populate the error response envelope.
     * </p>
     *
     * @param e       The domain-specific exception.
     * @param request The current web request context.
     * @return A {@link ResponseEntity} containing the structured error.
     */
    @ExceptionHandler(value = ScanlyBaseException.class)
    public ResponseEntity<HttpErrorResponse<Void>> handleException(
            ScanlyBaseException e, HttpServletRequest request) {
        HttpErrorResponse<Void> errorResponse = new HttpErrorResponse<>(
                e.getHttpStatus().value(),
                e.getTimestamp(),
                e.getErrorCode(),
                e.getMessage(),
                request.getRequestURI(),
                null
        );

        return new ResponseEntity<>(
                errorResponse,
                e.getHttpStatus()
        );
    }

    /**
     * Acts as the final catch-all for unhandled system exceptions.
     * <p>
     * Prevents internal stack traces or sensitive system details from leaking
     * to the client. Returns a generic 500 Internal Server Error message while
     * ensuring the response still follows the standard JSON format.
     * </p>
     *
     * @param e       The unexpected exception.
     * @param request The current web request context.
     * @return A 500 Internal Server Error response.
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<HttpErrorResponse<Void>> handleException(Exception e, HttpServletRequest request) {
        HttpErrorResponse<Void> errorResponse = new HttpErrorResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now(),
                "INTERNAL_SERVER_ERROR",
                "Something went wrong. Please try again later.",
                request.getRequestURI(),
                null
        );
        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * Handles JSR-303 / Bean Validation failures.
     * <p>
     * Triggered when an object annotated with {@code @Valid} fails validation.
     * It extracts the field-level details (field name and constraint message)
     * to help the frontend provide granular feedback.
     * </p>
     *
     * @param e       The validation exception containing BindingResult details.
     * @param request The current web request context.
     * @return A 400 Bad Request response with a list of {@link ValidationError} details.
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<HttpErrorResponse<ValidationError>> handleException(
            MethodArgumentNotValidException e, HttpServletRequest request
    ) {
        List<ValidationError> errors = getValidationErrors(e);
        HttpErrorResponse<ValidationError> errorResponse = new HttpErrorResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                Instant.now(),
                HttpStatus.BAD_REQUEST.toString(),
                "Bad Request",
                request.getRequestURI(),
                errors
        );

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Private helper to extract and map field errors from a validation exception.
     * @param e The validation exception.
     * @return A list of mapped {@link ValidationError} objects.
     */
    private List<ValidationError> getValidationErrors(MethodArgumentNotValidException e) {
        return e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map((error) -> new ValidationError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();
    }
}