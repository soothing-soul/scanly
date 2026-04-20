package com.scanly.otp.model;

/**
 * Represents the exhaustive set of outcomes for an OTP verification attempt.
 * <p>
 * This enum provides the calling domain with high-granularity feedback regarding
 * the state of a verification challenge. By returning these specific statuses,
 * the OTP service remains agnostic of business rules (e.g., whether to block an IP
 * or lock an account), leaving those decisions to the respective domain logic.
 */
public enum OtpVerificationStatus {

    /**
     * Indicates that a verification attempt is already being processed for the
     * given challenge ID.
     * <p>
     * This status is typically used in conjunction with distributed locking
     * to prevent race conditions or "double-spend" style attacks on the
     * verification process.
     * </p>
     */
    CONCURRENT_REQUEST,

    /**
     * The system could not find a record associated with the provided
     * challenge ID. This may occur if the ID is incorrect or the record
     * has been evicted from the persistence layer.
     */
    NOT_FOUND,

    /**
     * The challenge exists, but the current timestamp has surpassed the
     * expiration threshold defined during generation.
     */
    EXPIRED,

    /**
     * The provided OTP correctly matches the stored record, and the challenge
     * is within its valid lifecycle and attempt limits.
     */
    SUCCESS,

    /**
     * The provided OTP does not match the stored hash, but the user has
     * remaining attempts available.
     */
    INVALID_OTP,

    /**
     * The provided OTP is incorrect, and this failure has caused the
     * attempt counter to reach the defined maximum limit.
     * <p>
     * The calling domain should use this status to trigger security
     * protocols like cooling-off periods or MFA step-up requirements.
     * </p>
     */
    INVALID_OTP_MAXIMUM_ATTEMPT_REACHED,
}