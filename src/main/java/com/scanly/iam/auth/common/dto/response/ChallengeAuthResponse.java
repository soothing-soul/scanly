package com.scanly.iam.auth.common.dto.response;

/**
 * A specialized response for the generate challenge request.
 * <p>
 * This record is typically returned when the user requested to generate the OTP
 * through SMS, or EMAIL to indicate the status of OTP Generation.
 *
 * By default, it doesn't contain anything. In the future, it might contain additional
 * details like left attempts, or cooldown period etc.
 * </p>
 */
public record ChallengeAuthResponse(
) implements AuthResponse {
}