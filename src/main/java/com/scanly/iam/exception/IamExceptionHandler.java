package com.scanly.iam.exception;

import com.scanly.common.exception.ScanlyBaseException;
import com.scanly.common.web.exception.HttpErrorResponse;
import com.scanly.iam.policy.exception.EmailPolicyValidationException;
import com.scanly.iam.policy.exception.PasswordPolicyValidationException;
import com.scanly.iam.policy.model.PolicyValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

/**
 * Global exception handler for the IAM module.
 * <p>
 * This class uses {@link RestControllerAdvice} to intercept exceptions thrown by
 * any controller within the {@code com.scanly.iam} package. It ensures that
 * security and policy-related errors are returned to the client with a
 * consistent structure and the correct HTTP status codes.
 * <p>
 * It is configured with {@link Ordered#HIGHEST_PRECEDENCE} to ensure IAM-specific
 * logic is applied before any generic system-wide exception handlers.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.scanly.iam")
public class IamExceptionHandler {

    /**
     * Handles policy violations by converting the exception into a structured
     * error response containing all specific validation failures.
     *
     * @param ex      The intercepted {@link PasswordPolicyValidationException} or {@link EmailPolicyValidationException}.
     * @param request The current HTTP request, used to extract the URI for the response metadata.
     * @return A {@link ResponseEntity} containing a {@link HttpErrorResponse} populated
     * with detailed policy errors.
     */
    @ExceptionHandler(exception = {PasswordPolicyValidationException.class, EmailPolicyValidationException.class})
    public ResponseEntity<HttpErrorResponse<PolicyValidationError>> handlePasswordPolicyValidationException(
            ScanlyBaseException ex, HttpServletRequest request
    ) {
        // Build the standardized wrapper for API errors
        HttpErrorResponse<PolicyValidationError> errorResponse = new HttpErrorResponse<>(
                ex.getHttpStatus().value(),
                ex.getTimestamp(),
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                getPolicyValidationErrorDetail(ex)
        );

        return new ResponseEntity<>(
                errorResponse,
                ex.getHttpStatus()
        );
    }

    /**
     * Extracts granular policy errors from the exception's context.
     * <p>
     * Since {@link PasswordPolicyValidationException} can aggregate multiple
     * failures (e.g., length and special characters), this helper method
     * transforms the raw context map into a list of {@link PolicyValidationError} objects.
     *
     * @param ex The exception containing the error context.
     * @return A list of structured error objects for the API response.
     */
    private List<PolicyValidationError> getPolicyValidationErrorDetail(ScanlyBaseException ex) {
        Map<String, Object> errorMap = ex.getContext();

        // Convert the internal context Map into a list of DTOs for JSON serialization
        return errorMap.values()
                .stream()
                .map(value -> (PolicyValidationError) value)
                .toList();
    }
}