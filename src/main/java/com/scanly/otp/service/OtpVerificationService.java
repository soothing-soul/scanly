package com.scanly.otp.service;

import com.scanly.common.hashing.HashingService;
import com.scanly.infra.cache.redis.RedisLock;
import com.scanly.otp.model.*;
import com.scanly.otp.persistence.OtpCache;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Internal service responsible for the atomic verification of OTP challenges.
 * <p>
 * This service implements the core validation logic, ensuring that verification
 * attempts are synchronized via distributed locks to prevent race conditions.
 * It manages the lifecycle of the {@link OtpState} by either updating failure
 * counts or purging the state upon terminal conditions (success, expiration,
 * or exhausted attempts).
 * </p>
 */
@Service
class OtpVerificationService {
    private final OtpCache otpCache;
    private final HashingService hashingService;

    OtpVerificationService(OtpCache otpCache, HashingService hashingService) {
        this.otpCache = otpCache;
        this.hashingService = hashingService;
    }

    /**
     * Entry point for OTP verification. Handles infrastructure concerns
     * such as distributed locking and initial state retrieval.
     *
     * @param context The user's input and the associated challenge ID.
     * @return The resulting {@link OtpVerificationStatus}.
     */
    public OtpVerificationStatus verify(OtpVerificationContext context) {
        String challengeId = context.challengeId();
        RedisLock lock = otpCache.acquireLock(challengeId);

        if (lock == null) {
            return OtpVerificationStatus.CONCURRENT_REQUEST;
        }

        try {
            // Functional flow: Map the state to processing logic if present,
            // otherwise return NOT_FOUND.
            return otpCache.getOtpState(challengeId)
                    .map(state -> processVerification(state, context.otp()))
                    .orElse(OtpVerificationStatus.NOT_FOUND);
        } finally {
            otpCache.releaseLock(lock);
        }
    }

    /**
     * Contains the core state-transition logic for a verification attempt.
     * <p>
     * This method evaluates the challenge against the provided input and
     * determines if the state should be updated (failure) or purged (terminal).
     * </p>
     *
     * @param state  The current {@link OtpState} retrieved from cache.
     * @param rawOtp The unhashed OTP provided by the user.
     * @return The status representing the outcome of this specific process.
     */
    private OtpVerificationStatus processVerification(OtpState state, String rawOtp) {
        String challengeId = state.challengeId();

        // 1. Check for Expiration
        if (state.isExpired()) {
            otpCache.deleteOtpState(challengeId);
            return OtpVerificationStatus.EXPIRED;
        }

        // 2. Check for successful Match
        if (isMatch(state, rawOtp)) {
            otpCache.deleteOtpState(challengeId);
            return OtpVerificationStatus.SUCCESS;
        }

        // 3. Handle Failure and Thresholds
        if (state.canFailureBreachThreshold()) {
            otpCache.deleteOtpState(challengeId);
            return OtpVerificationStatus.INVALID_OTP_MAXIMUM_ATTEMPT_REACHED;
        }

        // 4. Update state for a non-terminal failure
        OtpState updatedOtpState = state.incrementFailedAttempts();
        otpCache.updateOtpState(updatedOtpState, challengeId);
        return OtpVerificationStatus.INVALID_OTP;
    }

    /**
     * Securely compares the provided OTP against the stored hash.
     */
    private boolean isMatch(OtpState state, String rawOtp) {
        return hashingService.verify(state.otpHash(), rawOtp);
    }
}