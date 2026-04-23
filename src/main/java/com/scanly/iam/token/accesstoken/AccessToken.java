package com.scanly.iam.token.accesstoken;

import java.time.Instant;

/**
 * An immutable data carrier representing a signed access credential within the Scanly
 * IAM domain.
 * <p>
 * This record serves as a specialized Data Transfer Object (DTO) used to communicate the
 * result of an access token generation event. It encapsulates both the raw credential
 * string and its lifecycle metadata, ensuring that consuming layers (such as the
 * orchestrator or REST controllers) have the necessary information to handle token expiration.
 * <p>
 *
 * @param token     The cryptographically signed string (typically a JWT) that grants
 * access to protected resources.
 * @param expiresAt The {@link Instant} marking the precise moment this token becomes
 * invalid. Used by clients to proactively manage session refreshing.
 */
public record AccessToken(
        /**
         * The encoded token value. This is the "secret" string that will be included
         * in the 'Authorization: Bearer' header for authenticated requests.
         */
        String token,

        /**
         * Absolute expiration timestamp. Providing this as an Instant allows the system
         * to remain timezone-agnostic and simplifies TTL (Time-to-Live) calculations for
         * client-side caching.
         */
        Instant expiresAt
) {
}
