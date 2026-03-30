package com.scanly.iam.exception;

import com.scanly.common.exception.ScanlyError;
import org.springframework.http.HttpStatus;

/**
 * Defines the catalog of standardized error types for the IAM (Identity and Access Management) module.
 * <p>
 * Each enum member maps a high-level business failure (like a policy violation)
 * to a specific {@code errorCode} used by the frontend and a {@link HttpStatus}
 * used by the REST layer.
 * <p>
 * This enumeration is used as a mandatory parameter for {@link IamException}
 * and its subclasses to ensure type-safe error reporting.
 */
public enum IamError implements ScanlyError {

    /** Indicates that the email already exists in the system */
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT),

    /** Indicates that the provided email failed one or more security or business policies. */
    EMAIL_POLICY_VALIDATION_FAILURE("EMAIL_POLICY_VALIDATION_FAILURE", HttpStatus.BAD_REQUEST),

    /** Indicates that the password does not meet complexity, length, or history requirements. */
    PASSWORD_POLICY_VALIDATION_FAILURE("PASSWORD_POLICY_VALIDATION_FAILED", HttpStatus.BAD_REQUEST),

    /** A catch-all error for failures occurring during the user onboarding process. */
    REGISTRATION_FAILURE("REGISTRATION_FAILURE", HttpStatus.BAD_REQUEST);

    /** The unique string identifier for the error (e.g., used in JSON responses). */
    private final String errorCode;

    /** The HTTP status code to be returned to the client. */
    private final HttpStatus httpStatus;

    /**
     * Internal constructor for defining IAM error constants.
     *
     * @param errorCode  The machine-readable error string.
     * @param httpStatus The associated Spring HTTP status.
     */
    IamError(String errorCode, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Retrieves the unique error identifier.
     * @return The error code string.
     */
    @Override
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Retrieves the HTTP status associated with this error.
     * @return The {@link HttpStatus} object.
     */
    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}