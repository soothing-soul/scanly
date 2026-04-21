package com.scanly.otp.model;

/**
 * Represents the outcome of an OTP (One-Time Password) generation attempt.
 * <p>
 * This record acts as a Data Transfer Object (DTO) that encapsulates both the
 * status of the request and the generated code, if applicable.
 * <p>
 * <b>Note:</b> If the {@link #status()} is not {@link OtpGenerationStatus#SUCCESS},
 * the {@code otp} field will be {@code null}.
 *
 * @param status The outcome status of the generation request.
 * @param otp    The raw, generated one-time password string. Only populated on success.
 */
public record OtpGenerationResult(
        OtpGenerationStatus status,
        String otp
) {
    /**
     * Secondary constructor for generation results that do not produce a code.
     * <p>
     * Typically used when the status is {@link OtpGenerationStatus#CONCURRENT_REQUEST}
     * or other non-success states where the generation process was halted.
     * </p>
     *
     * @param status The outcome status of the request.
     */
    public OtpGenerationResult(OtpGenerationStatus status) {
        this(status, null);
    }
}