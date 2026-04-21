package com.scanly.otp.model;

/**
 * Represents the status of an OTP (One-Time Password) generation request.
 * <p>
 * This enum is used to communicate the outcome of the generation process
 * to the caller, distinguishing between successful creation and failures
 * caused by rate-limiting or overlapping requests.
 * </p>
 */
public enum OtpGenerationStatus {

    /**
     * The OTP was successfully generated and is ready for delivery
     * via the configured communication channel (e.g., SMS, Email).
     */
    SUCCESS,

    /**
     * The request was rejected because another OTP generation or verification
     * process is already in progress for the same identifier.
     */
    CONCURRENT_REQUEST
}