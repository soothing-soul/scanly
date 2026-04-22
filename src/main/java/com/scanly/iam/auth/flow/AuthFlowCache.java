package com.scanly.iam.auth.flow;

import com.scanly.iam.auth.flow.model.AuthFlowState;
import com.scanly.infra.cache.redis.RedisService;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Cache manager for tracking the state of active authentication flows.
 * <p>
 * This component uses Redis to persist {@link AuthFlowState} across multiple
 * HTTP requests, enabling stateful multistep authentication (e.g., MFA)
 * in a stateless microservice environment.
 * </p>
 */
@Component
public class AuthFlowCache {

    /**
     * Default Time-To-Live (TTL) for an authentication session.
     *
     * <p>
     *     <b>Note:</b> This is the duration for which the record will remain in
     *     the redis. It is different from expiry of the session which is tracked
     *     through a different field inside the state itself.
     * </p>
     *
     */
    private static final Duration FLOW_TTL = Duration.ofMinutes(12);

    /** Namespace prefix for Redis keys to avoid collisions. */
    private static final String KEY_PREFIX = "auth:flow:%s";

    private final RedisService redisService;

    /**
     * Constructs the cache manager with a Redis integration.
     * @param redisService the infrastructure service for Redis operations
     */
    public AuthFlowCache(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * Generates a standardized Redis key for a specific flow ID.
     * @param flowId the unique identifier for the authentication attempt
     * @return a formatted string key
     */
    private String getFlowStateKey(String flowId) {
        return String.format(KEY_PREFIX, flowId);
    }

    /**
     * Persists the current state of an authentication flow.
     * <p>
     * The state is stored with a fixed expiration {@code FLOW_TTL} to ensure
     * stale or abandoned login attempts are automatically cleaned up.
     * </p>
     * @param flowId the unique identifier for the session
     * @param flowState the state data to persist
     */
    public void saveFlowState(String flowId, AuthFlowState flowState) {
        String key = getFlowStateKey(flowId);
        redisService.set(key, flowState, FLOW_TTL);
    }

    /**
     * Retrieves the current state of an authentication flow.
     * @param flowId the unique identifier for the session
     * @return an {@link Optional} containing the {@link AuthFlowState} if it exists
     * otherwise an empty Optional
     */
    public Optional<AuthFlowState> getFlowState(String flowId) {
        String key = getFlowStateKey(flowId);
        return redisService.get(key, AuthFlowState.class);
    }

    /**
     * Updates an existing flow state by overwriting it with new data while retaining
     * the TTL.
     *
     * @param flowId    The unique identifier for the session
     * @param flowState The updated state data
     */
    public boolean updateFlowState(String flowId, AuthFlowState flowState) {
        String key = getFlowStateKey(flowId);
        return redisService.update(key, flowState);
    }
}