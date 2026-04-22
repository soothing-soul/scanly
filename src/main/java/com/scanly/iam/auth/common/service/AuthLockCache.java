package com.scanly.iam.auth.common.service;

import com.scanly.infra.cache.redis.RedisLock;
import com.scanly.infra.cache.redis.RedisService;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * A specialized cache layer for managing distributed locks during the authentication process.
 * <p>
 * This component ensures that sensitive authentication operations (such as processing
 * an MFA challenge or updating security credentials) are synchronized per user.
 * This prevents race conditions and "double-submit" vulnerabilities in a distributed
 * environment.
 * </p>
 */
@Component
public class AuthLockCache {
    private final RedisService redisService;

    /**
     * Constructs the AuthLockCache with a required Redis infrastructure service.
     *
     * @param redisService The underlying service used to communicate with Redis.
     */
    public AuthLockCache(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * Attempts to acquire a distributed lock for a specific user.
     * <p>
     * The lock is scoped to the user ID to ensure that parallel requests for the
     * same user are processed sequentially, while requests for different users
     * remain non-blocking.
     * </p>
     *
     * @param userId The unique identifier of the user to be locked.
     * @return A {@link RedisLock} object if the lock was successfully acquired;
     * {@code null} if the user is currently locked by another process.
     */
    public RedisLock acquireLock(UUID userId) {
        // Formats the key as 'auth:user:<UUID>' to provide clear namespacing in Redis
        return redisService.acquireLock(
                String.format("lock:auth:user:%s", userId.toString()),
                Duration.ofSeconds(60) // 60-second safety timeout to prevent deadlocks
        );
    }

    /**
     * Safely releases a previously acquired user lock.
     *
     * @param lock The {@link RedisLock} instance to release.
     * @return {@code true} if the lock was successfully released; {@code false} otherwise.
     */
    public boolean releaseLock(RedisLock lock) {
        return redisService.releaseLock(lock);
    }
}