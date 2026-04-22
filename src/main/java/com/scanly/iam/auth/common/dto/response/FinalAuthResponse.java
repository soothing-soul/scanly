package com.scanly.iam.auth.common.dto.response;

import java.time.Instant;

/**
 * The successful completion of the authentication process.
 * <p>
 * This record contains the final credentials required for the client to access
 * protected resources. It fulfills the {@link AuthResponse} contract for
 * successful login or challenge completion scenarios.
 * </p>
 *
 * @param accessToken The Bearer token (typically a JWT) used for authorizing
 *                    subsequent API requests.
 * @param expiresAt   The timestamp representing the exact moment this token
 *                    becomes invalid.
 *
 * @see AuthResponse
 */
public record FinalAuthResponse(
        String accessToken,
        Instant expiresAt
) implements AuthResponse {}