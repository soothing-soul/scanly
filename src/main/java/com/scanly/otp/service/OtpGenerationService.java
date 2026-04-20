package com.scanly.otp.service;

import com.scanly.common.hashing.HashingService;
import com.scanly.otp.model.OtpGenerationContext;
import com.scanly.otp.model.OtpGenerationResult;
import com.scanly.otp.model.OtpState;
import com.scanly.otp.persistence.OtpCache;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * Internal service responsible for the creation and lifecycle initiation of
 * OTP challenges.
 * <p>
 * This class encapsulates the logic for generating a raw OTP, transforming it
 * into a secure hashed state, and ensuring it is persisted in the distributed
 * cache with an appropriate expiration window. It is kept package-private to
 * enforce the use of the {@link OtpService} facade.
 * </p>
 */
@Service
class OtpGenerationService {
    private final OtpCache otpCache;
    private final HashingService hashingService;

    /**
     * Constructs the generation service with necessary infrastructure dependencies.
     *
     * @param otpCache       The persistence layer for storing the OTP state.
     * @param hashingService The service used to one-way hash the OTP before
     * persistence for security.
     */
    OtpGenerationService(OtpCache otpCache, HashingService hashingService) {
        this.otpCache = otpCache;
        this.hashingService = hashingService;
    }

    /**
     * Executes the end-to-end generation flow for a new OTP challenge.
     * <p>
     * The method performs the following steps:
     * <ol>
     * <li>Generates a fresh 6-digit numeric OTP.</li>
     * <li>Creates an immutable {@link OtpState} containing the hashed OTP and metadata.</li>
     * <li>Saves the state to the cache with a TTL double the challenge's validity
     * period to allow for late-arriving requests or auditability.</li>
     * <li>Returns the raw OTP to be dispatched by the calling domain.</li>
     * </ol>
     * </p>
     *
     * @param otpGenerationContext The parameters defining the challenge's
     * constraints (TTL, max attempts, etc.).
     * @return An {@link OtpGenerationResult} containing the raw OTP.
     */
    public OtpGenerationResult generate(OtpGenerationContext otpGenerationContext) {
        String otp = OtpGenerator.generate();
        OtpState otpState = generateNewOtpState(otpGenerationContext, otp);

        // Use a buffer for cache persistence to ensure the record doesn't vanish
        // the exact millisecond the OTP expires.
        Duration otpRecordTtl = otpGenerationContext.ttl().multipliedBy(2);

        otpCache.saveOtpState(otpState, otpState.challengeId(), otpRecordTtl);
        return new OtpGenerationResult(otp);
    }

    /**
     * Maps the generation context and raw OTP into a persisted {@link OtpState} record.
     * <p>
     * This method handles the conversion of {@link Duration} into absolute
     * {@link Instant} timestamps and utilizes the {@link HashingService} to
     * ensure the raw OTP is never stored in plain text.
     * </p>
     *
     * @param otpGenerationContext The source configuration.
     * @param otp                  The raw OTP string to be hashed.
     * @return A fully initialized {@link OtpState} object.
     */
    private OtpState generateNewOtpState(OtpGenerationContext otpGenerationContext, String otp) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(otpGenerationContext.ttl().getSeconds());
        String otpHash = hashingService.hash(otp);

        return new OtpState(
                otpGenerationContext.challengeId(),
                otpGenerationContext.purpose(),
                0,
                otpGenerationContext.maxAttemptsAllowed(),
                now,
                expiresAt,
                otpHash
        );
    }
}