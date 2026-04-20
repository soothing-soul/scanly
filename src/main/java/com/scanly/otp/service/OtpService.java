package com.scanly.otp.service;

import com.scanly.otp.model.OtpGenerationContext;
import com.scanly.otp.model.OtpGenerationResult;
import com.scanly.otp.model.OtpVerificationContext;
import com.scanly.otp.model.OtpVerificationStatus;
import org.springframework.stereotype.Service;

/**
 * The primary entry point and facade for the OTP (One-Time Password) module.
 * <p>
 * This service provides a unified API for the rest of the Scanly application to
 * interact with OTP logic. Following the principle of <b>Separation of Concerns</b>,
 * this class does not contain the core logic itself but orchestrates the
 * interaction between specialized generation and verification services.
 * <p>
 *
 * By exposing only {@code generate} and {@code verify} methods, it maintains
 * a clean boundary, ensuring that consuming domains (like Identity or
 * Notification) remain agnostic of the underlying persistence and
 * cryptographic implementations.
 */
@Service
public class OtpService {
    private final OtpGenerationService otpGenerationService;
    private final OtpVerificationService otpVerificationService;

    /**
     * Constructs the facade with its required granular services.
     *
     * @param otpGenerationService   The service responsible for creating and
     * persisting new OTP challenges.
     * @param otpVerificationService The service responsible for validating
     * user input and managing attempt state.
     */
    public OtpService(OtpGenerationService otpGenerationService, OtpVerificationService otpVerificationService) {
        this.otpGenerationService = otpGenerationService;
        this.otpVerificationService = otpVerificationService;
    }

    /**
     * Orchestrates the creation of a new OTP challenge.
     * <p>
     * This method delegates the request to the generation service, which will
     * handle code generation, hashing, and cache persistence.
     * </p>
     *
     * @param otpGenerationContext The configuration (TTL, max attempts, etc.)
     * for the new challenge.
     * @return An {@link OtpGenerationResult} containing the raw OTP to be
     * sent to the user.
     */
    public OtpGenerationResult generate(OtpGenerationContext otpGenerationContext) {
        return otpGenerationService.generate(otpGenerationContext);
    }

    /**
     * Orchestrates the verification of a provided OTP against a challenge ID.
     * <p>
     * This method delegates to the verification service, which performs
     * distributed locking, hash comparison, and attempt tracking.
     * </p>
     *
     * @param otpVerificationContext The user-provided OTP and the associated
     * challenge ID.
     * @return An {@link OtpVerificationStatus} indicating the specific outcome
     * of the attempt.
     */
    public OtpVerificationStatus verify(OtpVerificationContext otpVerificationContext) {
        return otpVerificationService.verify(otpVerificationContext);
    }
}