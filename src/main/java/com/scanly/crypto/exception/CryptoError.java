package com.scanly.crypto.exception;

import com.scanly.common.exception.ScanlyError;
import org.springframework.http.HttpStatus;

/**
 * Enumerates the standardized error types for the cryptographic package.
 * <p>
 * This enum implements the {@link ScanlyError} interface, providing a bridge
 * between internal code failures and external API responses. Every error
 * is prefixed with {@code CRYPTO_ERROR_} to ensure uniqueness in global
 * application logs and frontend error handling.
 * </p>
 */
public enum CryptoError implements ScanlyError {

    /** The JVM does not support the requested cryptographic algorithm. */
    ALGORITHM_NOT_FOUND("ALGORITHM_NOT_FOUND", HttpStatus.INTERNAL_SERVER_ERROR),

    /** A requested Key ID (kid) does not exist in the vault. */
    KEY_NOT_FOUND("KEY_NOT_FOUND", HttpStatus.INTERNAL_SERVER_ERROR),

    /** A general failure occurred within the JCA or underlying crypto provider. */
    INTERNAL_CRYPTO_ERROR("INTERNAL_CRYPTO_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),

    /** The raw key material could not be read from the source (e.g., Disk I/O). */
    KEY_LOADING_FAILURE("KEY_LOADING_FAILURE", HttpStatus.INTERNAL_SERVER_ERROR),

    /** A key was used for an operation not allowed by its current status (e.g., signing with an expired key). */
    INVALID_KEY_OPERATION("INVALID_KEY_OPERATION", HttpStatus.INTERNAL_SERVER_ERROR),

    /** The system attempted to sign data but no 'ACTIVE' key is present in the vault. */
    NO_ACTIVE_KEY_AVAIlABLE("NO_ACTIVE_KEY_AVAIlABLE", HttpStatus.INTERNAL_SERVER_ERROR),

    /** Verification failed due to some issue with the system configuration, key missing */
    VERIFICATION_FAILED("VERIFICATION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR),

    /** The key material was retrieved but could not be parsed or decoded (e.g., malformed PEM). */
    KEY_ENCODING_FAILURE("KEY_ENCODING_FAILURE", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String prefix = "CRYPTO_ERROR_";
    private final String errorCode;
    private final HttpStatus httpStatus;

    CryptoError(String errorCode, HttpStatus httpStatus) {
        this.errorCode = prefix + errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Retrieves the fully qualified error code (e.g., CRYPTO_ERROR_KEY_NOT_FOUND).
     * @return The error code string.
     */
    @Override
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Retrieves the associated HTTP status for this error.
     * @return The {@link HttpStatus}.
     */
    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}