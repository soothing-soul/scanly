package com.scanly.otp.persistence;

import com.scanly.infra.cache.redis.RedisLock;
import com.scanly.infra.cache.redis.RedisService;
import com.scanly.otp.model.OtpState;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Persistence abstraction for managing the lifecycle of OTP states in a distributed cache.
 * <p>
 * This component acts as the data access layer for the OTP module, leveraging Redis
 * to ensure that OTP challenges are accessible across multiple service instances.
 * It provides mechanisms for state persistence, retrieval, and distributed locking
 * to handle concurrent verification attempts safely.
 * </p>
 */
@Component
public class OtpCache {
    private final RedisService redisService;

    /**
     * Constructs the cache component with a required {@link RedisService}.
     *
     * @param redisService The infrastructure service used for Redis operations.
     */
    public OtpCache(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * Generates a standardized Redis key for storing {@link OtpState}.
     *
     * @param challengeId The unique identifier for the OTP challenge.
     * @return A formatted string key (e.g., "otp:uuid-123").
     */
    private String getOtpStateKey(String challengeId) {
        return String.format(
                "otp:%s", challengeId
        );
    }

    /**
     * Persists a new OTP state with a specific time-to-live (TTL).
     *
     * @param otpState    The {@link OtpState} record containing challenge details.
     * @param challengeId The unique key for this challenge.
     * @param timeout     The duration for which this record should persist in Redis.
     */
    public void saveOtpState(OtpState otpState, String challengeId, Duration timeout) {
        String key = getOtpStateKey(challengeId);
        redisService.set(key, otpState, timeout);
    }

    /**
     * Retrieves the current state of an OTP challenge from the cache.
     *
     * @param challengeId The identifier used to locate the state.
     * @return An {@link Optional} containing the {@link OtpState} if found,
     * or empty if non-existent.
     */
    public Optional<OtpState> getOtpState(String challengeId) {
        String key = getOtpStateKey(challengeId);
        return redisService.get(key, OtpState.class);
    }

    /**
     * Updates an existing OTP state (e.g., after an incremented failure attempt).
     * <p>
     * This method ensures that the update operation will retain the existing TTL
     * set for the record.
     * </p>
     *
     * @param otpState    The updated {@link OtpState} record.
     * @param challengeId The unique key for the challenge.
     */
    public void updateOtpState(OtpState otpState, String challengeId) {
        String key = getOtpStateKey(challengeId);
        redisService.update(key, otpState);
    }

    /**
     * Explicitly removes an OTP challenge from the cache.
     * <p>
     * Typically called after a successful verification or when a domain
     * explicitly invalidates a challenge.
     * </p>
     *
     * @param challengeId The identifier to delete.
     * @return {@code true} if the deletion was successful.
     */
    public boolean deleteOtpState(String challengeId) {
        String key = getOtpStateKey(challengeId);
        return redisService.delete(key);
    }

    /**
     * Acquires a distributed lock for a specific challenge to prevent
     * race conditions during concurrent verification requests.
     *
     * @param challengeId The identifier representing the resource to lock.
     * @return A {@link RedisLock} object representing the acquired lock.
     */
    public RedisLock acquireLock(String challengeId) {
        String lockKey = String.format(
                "lock:otp:%s", challengeId
        );
        return redisService.acquireLock(lockKey, Duration.ofSeconds(60));
    }

    /**
     * Releases a previously acquired distributed lock.
     *
     * @param redisLock The lock instance to release.
     * @return {@code true} if the release was successful.
     */
    public boolean releaseLock(RedisLock redisLock) {
        return redisService.releaseLock(redisLock);
    }
}