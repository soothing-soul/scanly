package com.scanly.infra.cache.redis;

/**
 * A data holder representing a distributed lock acquired via Redis.
 * <p>
 * This class encapsulates the information required to identify and safely
 * release a lock in a distributed environment. It ensures that the process
 * attempting to perform an operation has exclusive access to the resource
 * defined by the {@code key}.
 * </p>
 */
public class RedisLock {

    /** The unique identifier of the resource being locked (e.g., "lock:auth:flow:123"). */
    private final String key;

    /**
     * The unique value assigned to this specific lock instance.
     * <p>
     * This is typically a UUID used to ensure that only the original requester
     * can release the lock, preventing "accidental unlocks" from concurrent
     * processes whose own locks may have expired.
     * </p>
     */
    private final String value;

    /**
     * Package-private constructor. Instances should be created by the
     * {@code RedisLockManager} or equivalent infrastructure service.
     *
     * @param key   The resource key.
     * @param value The unique lock ownership identifier.
     */
    RedisLock(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return The resource key associated with this lock.
     */
    public String getKey() {
        return key;
    }

    /**
     * @return The unique ownership value of this lock.
     */
    public String getValue() {
        return value;
    }
}