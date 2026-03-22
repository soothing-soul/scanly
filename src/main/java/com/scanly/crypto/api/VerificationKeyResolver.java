package com.scanly.crypto.api;

import com.scanly.crypto.exception.KeyNotFoundException;
import com.scanly.crypto.model.Jwk;

/**
 * Strategy interface for resolving public keys used in signature verification.
 * <p>
 * This interface is the "Verification Engine" entry point. It allows services
 * like {@code JwtDecoder} to dynamically find the exact public key that matches
 * the {@code kid} found in an incoming token's header.
 * </p>
 * <p>
 * <b>Implementation Note:</b> Real-world implementations may fetch keys from
 * a database, a JWKS (JSON Web Key Set) endpoint, or a local cache.
 * </p>
 */
public interface VerificationKeyResolver {

    /**
     * Retrieves a specific JWK by its unique identifier for signature verification.
     *
     * <p>
     * The returned {@link Jwk} object contains the Key ID (kid), the intended
     * algorithm, and the {@code PublicKey} which are mandatory for signature
     * verification.
     * </p>
     *
     * <p>
     * The implementation is also responsible to ensure only keys which are
     * currently eligible to verify signature are returned.
     * </p>
     *
     * @param kid The unique Key ID extracted from the JWS header.
     * @return The matching {@link Jwk} record containing public key material.
     * @throws KeyNotFoundException if the provided kid is unrecognized or
     * has been revoked from the system.
     */
    Jwk getVerificationKey(String kid);
}