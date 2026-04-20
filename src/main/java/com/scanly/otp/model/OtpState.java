package com.scanly.otp.model;

import java.time.Instant;

/**
 * Represents the immutable snapshot of an OTP challenge's current state.
 * <p>
 * This record maintains the integrity of the challenge, tracking expiration,
 * usage limits, and the cryptographic hash of the expected OTP. To maintain
 * thread safety and consistency—especially in distributed environments—state
 * transitions (like incrementing failures) result in the creation of a new instance.
 * </p>
 *
 * @param challengeId        The unique identifier for the challenge session.
 * @param purpose            The specific {@link OtpPurpose} this state is associated with.
 * @param totalFailedAttempts The current count of unsuccessful verification attempts.
 * @param maxAttemptsAllowed  The upper limit of failures permitted before the
 *                            challenge is considered exhausted.
 * @param createdAt          The timestamp when the challenge was first initialized.
 * @param expiresAt          The timestamp after which the OTP is no longer considered valid.
 * @param otpHash            The secure hash of the OTP value, used for verification
 * without storing the raw secret.
 */
public record OtpState(
        String challengeId,
        OtpPurpose purpose,
        int totalFailedAttempts,
        int maxAttemptsAllowed,
        Instant createdAt,
        Instant expiresAt,
        String otpHash
) {

    /**
     * Checks if the current system time has surpassed the expiration threshold.
     *
     * @return {@code true} if the current time is after {@code expiresAt},
     * otherwise {@code false}.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Predicts if a single additional failure will cause the challenge to
     * hit the maximum attempt limit.
     * <p>
     * This is useful for domain logic that provides "soft" warnings to the user
     * (e.g., "This is your last attempt before the code is invalidated").
     *
     * @return {@code true} if the next failure will meet the threshold.
     */
    public boolean canFailureBreachThreshold() {
        return totalFailedAttempts == (maxAttemptsAllowed - 1);
    }

    /**
     * Creates a new instance of {@code OtpState} with the failure counter
     * incremented by one.
     * <p>
     * This follows the functional approach to state management, ensuring the
     * original state remains unchanged and preventing side effects during
     * the verification process.
     *
     * @return A new {@code OtpState} instance with an updated failure count.
     */
    public OtpState incrementFailedAttempts() {
        return new OtpState(
                challengeId,
                purpose,
                totalFailedAttempts + 1,
                maxAttemptsAllowed,
                createdAt,
                expiresAt,
                otpHash
        );
    }
}