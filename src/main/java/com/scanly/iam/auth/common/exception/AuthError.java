package com.scanly.iam.auth.common.exception;

import com.scanly.common.exception.ScanlyError;
import org.springframework.http.HttpStatus;

/**
 * Defines the standardized catalog of authentication errors within the Scanly IAM ecosystem.
 * <p>
 * This enum implements {@link ScanlyError} to provide a consistent error-handling contract across
 * the application. Each constant maps a specific business failure (e.g., a violated sequence
 * or an expired session) to a machine-readable code and a semantic {@link HttpStatus}.
 * </p>
 */
public enum AuthError implements ScanlyError {

    /** The user attempted an action (e.g., "RESEND") that is not allowed in the current state. */
    AUTH_ACTION_NOT_PERMITTED("AUTH_ACTION_NOT_PERMITTED", HttpStatus.BAD_REQUEST),

    /** The user attempted an action (e.g., "GENERATE") that is not supported for TOTP. */
    AUTH_ACTION_NOT_SUPPORTED("AUTH_ACTION_NOT_SUPPORTED", HttpStatus.BAD_REQUEST),

    /** The authentication method chosen is not supported or not enabled for the specific user account. */
    AUTH_METHOD_NOT_PERMITTED("AUTH_METHOD_NOT_PERMITTED", HttpStatus.BAD_REQUEST),

    /** The authentication session or flow has timed out and must be restarted from the beginning. */
    AUTH_SESSION_EXPIRED("AUTH_SESSION_EXPIRED", HttpStatus.UNAUTHORIZED),

    /** The authentication step the user is attempting to perform has already been successfully validated. */
    AUTH_STEP_ALREADY_COMPLETED("AUTH_STEP_ALREADY_COMPLETED", HttpStatus.CONFLICT),

    /** Steps were performed out of order (e.g., attempting MFA verification before providing a password). */
    AUTH_STEP_SEQUENCE_VIOLATED("AUTH_STEP_SEQUENCE_VIOLATED", HttpStatus.BAD_REQUEST),

    /** There are multiple requests for the same authentication flow or same step. */
    CONCURRENT_REQUEST_ERROR("CONCURRENT_REQUEST_ERROR", HttpStatus.TOO_MANY_REQUESTS),

    /** The requested {@code currentAuthAction} string is not recognized by the system. */
    INVALID_AUTH_ACTION("INVALID_AUTH_ACTION", HttpStatus.BAD_REQUEST),

    /** The requested {@code authMethod} string is not recognized by the system. */
    INVALID_AUTH_METHOD("INVALID_AUTH_METHOD", HttpStatus.BAD_REQUEST),

    /** Requested {@code flowId} doesn't exist */
    INVALID_AUTH_SESSION("INVALID_AUTH_SESSION", HttpStatus.BAD_REQUEST),

    /** General failure for incorrect primary credentials (e.g., username or password). */
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED),

    /** The provided MFA token is malformed, has been tampered with, or does not belong to the current flow. */
    INVALID_MFA_TOKEN("INVALID_MFA_TOKEN", HttpStatus.UNAUTHORIZED),

    /** OTP for the given challengeId does not exist, or it has been deleted due to TTL*/
    OTP_NOT_FOUND("OTP_NOT_FOUND", HttpStatus.UNAUTHORIZED),

    /** OTP has expired and cannot be used for verification anymore */
    OTP_EXPIRED("OTP_EXPIRED", HttpStatus.UNAUTHORIZED),

    /** Given OTP does not match the OTP corresponding to the given challengeId */
    INVALID_OTP("INVALID_OTP", HttpStatus.UNAUTHORIZED),

    /** OTP is invalid and the failed attempt has reached the maximum allowed limit */
    INVALID_OTP_ATTEMPT_LIMIT_REACHED("INVALID_OTP_ATTEMPT_LIMIT_REACHED", HttpStatus.UNAUTHORIZED),

    /** The user account exists but is currently deactivated, locked, or pending administrative approval. */
    USER_NOT_ACTIVE("USER_NOT_ACTIVE", HttpStatus.FORBIDDEN),

    /** No user account was found matching the provided identifier. */
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND);

    /** The unique string identifier for the error (e.g., used in JSON responses for client-side logic). */
    private final String errorCode;

    /** The HTTP status code to be returned in the response header. */
    private final HttpStatus httpStatus;

    /**
     * Internal constructor for defining IAM error constants.
     *
     * @param errorCode  The machine-readable error string.
     * @param httpStatus The associated Spring {@link HttpStatus}.
     */
    AuthError(String errorCode, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Retrieves the unique error identifier.
     * * @return A non-null string representing the error code.
     */
    @Override
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Retrieves the HTTP status associated with this specific error.
     * * @return The {@link HttpStatus} assigned to this error.
     */
    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}