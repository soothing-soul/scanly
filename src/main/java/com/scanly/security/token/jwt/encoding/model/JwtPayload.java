package com.scanly.security.token.jwt.encoding.model;

import com.scanly.security.token.jwt.encoding.api.JwtEncoder;

import java.time.Duration;
import java.util.Map;

/**
 * A data transfer object representing the intended content of a JWT.
 * <p>
 * This record is used to pass claims and configuration data to the {@link JwtEncoder}.
 *
 * It presents overloaded methods to support the custom expiry values for different
 * services.
 * </p>
 *
 * @param timeToLive The "Time To Live": A duration used to calculate the "exp" (Expiration) claim.
 * @param claims A map of additional custom claims to be included in the JWT payload.
 */
public record JwtPayload(
        Duration timeToLive,
        Map<String, Object> claims
) {
    /**
     * Canonical constructor with defensive copying of the claims map.
     */
    public JwtPayload (Duration timeToLive, Map<String, Object> claims) {
        this.timeToLive = timeToLive;
        // Defensive copy ensures the payload is truly immutable
        this.claims = Map.copyOf(claims);
    }

    /**
     * Overload for creating a payload with custom claims but a default TTL.
     */
    public JwtPayload(Map<String, Object> claims) {
        this(null, Map.copyOf(claims));
    }
}