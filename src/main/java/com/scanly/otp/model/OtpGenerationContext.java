package com.scanly.otp.model;

import java.time.Duration;

/**
 * A record that carries the necessary parameters for generating a new OTP
 * challenge.
 *
 * <p>
 * This record acts as a "specification" for the OTP service, ensuring that
 * the lifecycle of the challenge (expiration and retry limits) is defined
 * at the moment of creation.
 * </p>
 *
 * @param challengeId        A unique identifier for the challenge (e.g., a UUID or
 * a correlated session ID).
 * @param purpose            The {@link OtpPurpose} describing why this OTP is being
 * generated.
 * @param maxAttemptsAllowed The maximum number of verification failures permitted
 * before the challenge is considered exhausted.
 * @param ttl                The "Time-To-Live" duration, specifying how long the
 * OTP remains valid before expiring.
 */
public record OtpGenerationContext(
        String challengeId,
        OtpPurpose purpose,
        int maxAttemptsAllowed,
        Duration ttl
) {
}