package com.scanly.otp.service;

import com.scanly.common.hashing.HashingService;
import com.scanly.infra.cache.redis.RedisLock;
import com.scanly.otp.model.OtpGenerationContext;
import com.scanly.otp.model.OtpGenerationResult;
import com.scanly.otp.model.OtpGenerationStatus;
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
     * Executes the end-to-end generation flow for a new OTP challenge with concurrency protection.
     * <p>
     * This method ensures mutual exclusion using a distributed lock based on the
     * {@code challengeId}. The workflow is as follows:
     * <ol>
     * <li>Attempts to acquire a {@link RedisLock} for the specific challenge.</li>
     * <li>If the lock is unavailable, returns a result with {@link OtpGenerationStatus#CONCURRENT_REQUEST}.</li>
     * <li>Generates a cryptographically secure 6-digit numeric OTP.</li>
     * <li>Creates an immutable {@link OtpState} containing the hashed OTP and metadata.</li>
     * <li>Persists the state to the cache with a TTL <b>double</b> the validity period
     * to support auditability and late-arrival verification.</li>
     * <li>Releases the lock and returns the raw OTP to the calling domain for delivery.</li>
     * </ol>
     * </p>
     *
     * @param otpGenerationContext The parameters defining the challenge constraints (TTL, max attempts, etc.).
     * @return An {@link OtpGenerationResult} indicating either success with the raw OTP,
     * or a concurrent request failure.
     */
    public OtpGenerationResult generate(OtpGenerationContext otpGenerationContext) {
        RedisLock lock = otpCache.acquireLock(otpGenerationContext.challengeId());

        if (lock == null) {
            return new OtpGenerationResult(
                    OtpGenerationStatus.CONCURRENT_REQUEST
            );
        }

        try {
            String otp = OtpGenerator.generate();
            OtpState otpState = generateNewOtpState(otpGenerationContext, otp);
            Duration otpRecordTtl = otpGenerationContext.ttl().multipliedBy(2);
            otpCache.saveOtpState(otpState, otpState.challengeId(), otpRecordTtl);

            return new OtpGenerationResult(
                    OtpGenerationStatus.SUCCESS,
                    otp
            );
        } finally {
            otpCache.releaseLock(lock);
        }
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