package com.scanly.iam.auth.email;

/**
 * A data transfer record containing the user-provided credentials for
 * an email OTP verification step.
 *
 * <p>
 * This payload is typically wrapped within a {@code StepAuthRequest}
 * and delivered to the {@link EmailOtpVerificationExecutor}. It carries
 * the raw input required to transition from a "Challenge" state to a
 * "Verified" state in the authentication lifecycle.
 * </p>
 *
 * @param otp The raw One-Time Password string entered by the user,
 * usually received via email.
 */
public record EmailOtpVerificationPayload(
        String otp
) {
}