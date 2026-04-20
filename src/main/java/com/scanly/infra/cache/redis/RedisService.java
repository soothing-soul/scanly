    package com.scanly.infra.cache.redis;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.springframework.data.redis.connection.RedisStringCommands;
    import org.springframework.data.redis.core.RedisCallback;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.data.redis.core.StringRedisTemplate;
    import org.springframework.data.redis.core.script.RedisScript;
    import org.springframework.data.redis.core.types.Expiration;
    import org.springframework.data.redis.serializer.RedisSerializer;
    import org.springframework.stereotype.Service;

    import java.time.Duration;
    import java.util.List;
    import java.util.Optional;
    import java.util.UUID;

    /**
     * Service providing high-level abstraction for Redis operations, including
     * typed object storage and distributed locking.
     * <p>
     * This service leverages Jackson for object mapping, allowing complex DTOs
     * to be stored as JSON while maintaining type safety during retrieval.
     * </p>
     */
    @Service
    public class RedisService {
        private final ObjectMapper mapper;

        /** Default expiration time (5 minutes) for cached items when not specified. */
        private final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(5);

        private final RedisTemplate<String, Object> redisTemplate;
        private final StringRedisTemplate stringRedisTemplate;

        /**
         * Lua script for atomic lock release.
         * Ensures that a lock is only deleted if the value matches, preventing
         * a process from accidentally releasing a lock held by another process.
         */
        private static final RedisScript<Long> RELEASE_LOCK_SCRIPT = RedisScript.of(
                """
                if redis.call('GET', KEYS[1]) == ARGV[1] then
                    return redis.call('DEL', KEYS[1])
                else
                    return 0
                end
                """,
                Long.class
        );

        public RedisService(ObjectMapper mapper, RedisTemplate<String, Object> redisTemplate,
                            StringRedisTemplate stringRedisTemplate
        ) {
            this.mapper = mapper;
            this.redisTemplate = redisTemplate;
            this.stringRedisTemplate = stringRedisTemplate;
        }

        /**
         * Stores an object in Redis with a specific time-to-live (TTL).
         *
         * @param key     The cache key.
         * @param value   The object to store.
         * @param timeout The duration before the key expires.
         */
        public <T> void set(String key, T value, Duration timeout) {
            redisTemplate.opsForValue().set(key, value, timeout);
        }

        /**
         * Stores an object in Redis with an optional default TTL.
         *
         * <p>
         *     <b>Note:</b> boolean value has been intentionally introduced
         *     to ensure that the caller doesn't create a perpetual key by
         *     mistake.
         * </p>
         *
         * @param key         The cache key.
         * @param value       The object to store.
         * @param ttlRequired If true, applies {@link #DEFAULT_TIMEOUT}.
         */
        public <T> void set(String key, T value, boolean ttlRequired) {
            if (ttlRequired) {
                set(key, value, DEFAULT_TIMEOUT);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
        }

        /**
         * Retrieves an object from Redis and converts it to the specified type.
         *
         * @param key   The cache key.
         * @param clazz The target class for conversion.
         * @return An {@link Optional} containing the typed object, or empty if not found.
         */
        public <T> Optional<T> get(String key, Class<T> clazz) {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(mapper.convertValue(value, clazz));
        }

        /**
         * Updates an existing key with a new value while preserving its current TTL.
         * <p>
         * This uses the Redis {@code SET ... KEEPTTL XX} command pattern.
         * The {@code XX} option ensures that the operation only succeeds if the key
         * already exists, preventing the accidental creation of persistent keys
         * that should have expired.
         * </p>
         *
         * @param key   The cache key to update.
         * @param value The new object state to store.
         * @param <T>   The type of the value.
         */
        public <T> void update(String key, T value) {
            redisTemplate.execute((RedisCallback<Boolean>) connection -> {
                byte[] rawKey = serializeKey(key);
                byte[] rawValue = serializeValue(value);

                /*
                 * KEEPTTL: Retain the existing expiration associated with the key.
                 * SET_IF_PRESENT (XX): Only perform the set if the key already exists.
                 */
                return connection.stringCommands().set(
                        rawKey,
                        rawValue,
                        Expiration.keepTtl(),
                        RedisStringCommands.SetOption.ifPresent()
                );
            });
        }

        /**
         * Helper to serialize keys using the template's configured StringSerializer.
         */
        private byte[] serializeKey(String key) {
            RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
            return serializer.serialize(key);
        }

        /**
         * Helper to serialize values using the template's configured ValueSerializer.
         * This ensures the format matches what opsForValue().set() produces.
         */
        private byte[] serializeValue(Object value) {
            RedisSerializer<Object> serializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();
            return serializer.serialize(value);
        }

        /**
         * Checks if a key exists in Redis.
         *
         * @param key The key to check.
         * @return {@code true} if the key exists.
         */
        public boolean exists(String key) {
            return redisTemplate.hasKey(key);
        }

        /**
         * Deletes a key from Redis.
         *
         * @param key The key to remove.
         * @return {@code true} if the deletion was successful.
         */
        public boolean delete(String key) {
            return redisTemplate.delete(key);
        }

        /**
         * Attempts to acquire a distributed lock on a specific key.
         * <p>
         * Uses the {@code SET NX PX} pattern to ensure atomicity.
         * </p>
         *
         * @param lockKey The identifier for the resource to lock.
         * @param timeout The duration for which the lock remains valid.
         * @return A {@link RedisLock} if successful, or {@code null} if the lock is held elsewhere.
         */
        public RedisLock acquireLock(String lockKey, Duration timeout) {
            String lockValue = UUID.randomUUID().toString();
            Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, timeout);
            return Boolean.TRUE.equals(result)
                    ? new RedisLock(lockKey, lockValue)
                    : null;
        }

        /**
         * Releases a distributed lock using an atomic Lua script.
         *
         * @param lock The lock instance to release.
         * @return {@code true} if the lock was successfully released by the owner.
         */
        public boolean releaseLock(RedisLock lock) {
            Long result = redisTemplate.execute(
                    RELEASE_LOCK_SCRIPT,
                    List.of(lock.getKey()),
                    lock.getValue()
            );
            return Long.valueOf(1).equals(result);
        }
    }