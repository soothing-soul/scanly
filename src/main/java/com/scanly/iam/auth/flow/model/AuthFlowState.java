package com.scanly.iam.auth.flow.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the complete persistent state of an ongoing authentication journey.
 * <p>
 * This object is serialized and stored in the cache (e.g., Redis) under a
 * unique {@code flowId}. It tracks the user's progress through multiple
 * authentication gates, ensuring that factors are completed in the correct
 * sequence and that security tokens are validated against specific steps.
 * </p>
 *
 * @param userId        The unique identifier of the user attempting to log in.
 * @param createdAt     Time at which the record was created
 * @param expiresAt     Time at which the session will expire
 * @param requiredSteps A sequential list of {@link AuthStep}s that the user
 *                      must satisfy to complete the flow.
 * @param mfaTokens     A mapping of method-specific identifiers to their
 *                      corresponding security tokens (e.g., "SMS" -> "token_abc").
 */
public record AuthFlowState(
        UUID userId,
        Instant createdAt,
        Instant expiresAt,
        List<AuthStep> requiredSteps,
        Map<String, String> mfaTokens
) {
    /**
     * Convenience method to tell whether the given authentication state has expired
     */
    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }

    /**
     * Helper method to update the steps and mfa tokens for the current step during
     * state transitions while retaining the fixed parameters like {@code userId}.
     */
    public AuthFlowState update(List<AuthStep> steps, Map<String, String> mfaTokens) {
        return new AuthFlowState(
                userId,
                createdAt,
                expiresAt,
                steps,
                mfaTokens
        );
    }
}