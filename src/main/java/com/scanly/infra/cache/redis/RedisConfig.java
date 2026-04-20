package com.scanly.infra.cache.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Infrastructure configuration for Redis-based caching and state management.
 * <p>
 * This class defines the bean requirements for interacting with Redis.
 * </p>
 */
@Configuration
public class RedisConfig {

    /**
     * Configures and provides a {@link RedisTemplate} for high-level data operations.
     * <p>
     * <b>Serialization Strategy:</b>
     * <ul>
     * <li><b>Keys:</b> Uses {@link StringRedisSerializer} to keep keys human-readable
     * in the Redis database (e.g., "auth:flow:123").</li>
     * <li><b>Values:</b> Uses {@link Jackson2JsonRedisSerializer} to store Java
     * objects as JSON strings. This ensures interoperability and allows for
     * schema evolution without the brittleness of standard Java serialization.</li>
     * </ul>
     * </p>
     *
     * @param redisConnectionFactory The underlying connection factory (injected by Spring
     * based on application properties).
     * @return A thread-safe {@code RedisTemplate} for key-value operations.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            ObjectMapper objectMapper,
            RedisConnectionFactory redisConnectionFactory
    ) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // Key serialization: Simple strings are best for CLI visibility
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Value serialization: JSON allows us to store complex Records and DTOs
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        return redisTemplate;
    }
}