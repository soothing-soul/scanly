package com.scanly.otp.model;

/**
 * Represents the successful outcome of an OTP generation request.
 * <p>
 * This record serves as the data transfer object (DTO) returned by the OTP service
 * after a challenge has been successfully initialized and persisted.
 * <p>
 * Since the OTP service is agnostic of delivery mechanisms, the consuming domain
 * is responsible for extracting the {@code otp} from this result and dispatching
 * it to the user via the required communication channel (e.g., MailService).
 *
 * @param otp The raw, generated one-time password string.
 */
public record OtpGenerationResult(
        String otp
) {
}