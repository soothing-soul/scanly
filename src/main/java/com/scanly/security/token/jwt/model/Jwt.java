package com.scanly.security.token.jwt.model;

import com.scanly.security.token.jwt.constants.JwtConstants;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * A library-agnostic representation of a JSON Web Token (JWT).
 * This record serves as the unified domain model for the Scanly platform,
 * abstracting away the specifics of underlying security libraries
 * (like Nimbus or Spring Security). Irrespective of any changes in the
 * underlying implementation in the future, scanly platform will continue
 * to use this record.
 *
 * It provides type-safe accessors for standard and custom claims.
 *
 * @param iat The Instant the token was issued.
 * @param exp The Instant the token expires.
 * @param jti The unique JWT ID (used for preventing replay attacks).
 * @param headers The raw JOSE header map.
 * @param claims The raw JWT payload claims map.
 * @param token The original encoded string (header.payload.signature).
 */
public record Jwt(
        Instant iat,
        Instant exp,
        String jti,
        Map<String,Object> headers,
        Map<String,Object> claims,
        String token
) {
    /**
     * @return the subject of the token
     */
    public String getSubject() {
        return getClaimAsString(JwtConstants.SUBJECT);
    }

    /**
     * @return the audience of the token
     */
    public String getAudience() {
        return getClaimAsString(JwtConstants.AUDIENCE);
    }

    /**
     * @return whether the token is expired or not
     */
    public boolean isExpired() {
        return exp.isBefore(Instant.now());
    }

    /**
     * @return Instant at which the token was issued
     */
    public Instant getIssuedAt() {
        return iat;
    }

    /**
     * @return Instant at which the token expires
     */
    public Instant getExpiresAt() {
        return exp;
    }

    /**
     * @return Issuer of the token
     */
    public String getIssuer() {
        return getClaimAsString(JwtConstants.ISSUER);
    }

    /**
     * @return ID of the token
     */
    public String getJwtId() {
        return jti;
    }

    /**
     * @param key Key in the claims map
     * @return value if present, else null
     */
    public String getClaimAsString(String key) {
        return Optional.ofNullable(claims.get(key))
                .map(Object::toString)
                .orElse(null);
    }

    /**
     * @param key Key in the claims map
     * @return Boolean value of the claim if present, else null
     */
    public Boolean getClaimAsBoolean(String key) {
        Object claim =  claims.get(key);
        if (claim instanceof Boolean b) {
            return b;
        } else if  (claim instanceof String) {
            return Boolean.parseBoolean((String) claim);
        }
        return null;
    }

    /**
     * @param key Key in the claims map
     * @return Long value of the claim if present, else null
     */
    public Long getClaimAsLong(String key) {
        Object claim =  claims.get(key);
        if (claim instanceof Number n) {
            return n.longValue();
        } else if  (claim instanceof String s) {
            return Long.parseLong(s);
        }
        return null;
    }
}
