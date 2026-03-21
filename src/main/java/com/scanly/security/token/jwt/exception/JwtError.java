package com.scanly.security.token.jwt.exception;

import com.scanly.common.exception.ScanlyError;
import org.springframework.http.HttpStatus;

/**
 * Defines the specific error categories for failures within the JWT infrastructure.
 * <p>
 * This enum maps technical failures to specific error codes and HTTP statuses,
 * allowing the Scanly platform to provide consistent, machine-readable error responses.
 */
public enum JwtError implements ScanlyError {

    /**
     * Occurs when the requested cryptographic algorithm is not supported by the
     * underlying JCE (Java Cryptography Extension) or the Scanly crypto package.
     */
    ALGORITHM_NOT_SUPPORTED("ALGORITHM_NOT_SUPPORTED", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * A general failure during the JWS signing process. This typically indicates
     * an issue with the private key or the signing provider.
     */
    SIGNING_FAILURE("SIGNING_FAILURE", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * Specifically handles issues when converting between raw ECDSA signatures
     * and the format required by the JWS specification (R+S concatenation).
     */
    ECDSA_SIGNATURE_TRANSCODE_ERROR("ECDSA_SIGNATURE_TRANSCODE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * Handles the case where the requested expiry exceeds the maximum permissible limit
     * as per the configured values.
     */
    EXPIRY_LIMIT_BREACH("EXPIRY_LIMIT_BREACH", HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * Signal that a JWT failed its integrity or validity check during a manual
     * decoding process.
     * <p>
     * Unlike standard access token filters, this error is intended for downstream
     * business services (e.g., PDF signature verification, QR payload validation).
     * Callers should catch {@link JwtVerificationException} and implement
     * specific recovery or rejection logic.
     * </p>
     * Mapped to 500 by default as a fail-fast mechanism for unhandled validation errors.
     */
    VERIFICATION_FAILURE("VERIFICATION_FAILURE", HttpStatus.INTERNAL_SERVER_ERROR),;

    private final String prefix = "JWT_ERROR_";
    private final String errorCode;
    private final HttpStatus httpStatus;

    JwtError(String errorCode, HttpStatus httpStatus) {
        this.errorCode = prefix + errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Returns the full, prefixed error code (e.g., "JWT_ERROR_SIGNING_FAILURE").
     *
     * @return The unique string identifier for this error.
     */
    @Override
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the HTTP status code that should be sent to the client when this error occurs.
     *
     * @return The {@link HttpStatus} associated with this error.
     */
    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}