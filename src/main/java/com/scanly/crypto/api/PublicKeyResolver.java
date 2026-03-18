package com.scanly.crypto.api;

import com.scanly.crypto.exception.KeyNotFoundException;
import com.scanly.crypto.exception.NoActiveKeyAvailableException;
import com.scanly.crypto.model.Jwk;

/**
 * High-level API for resolving public keys and selecting the current signing identity.
 * <p>
 * This interface provides a safe, read-only view of the platform's cryptographic
 * identities. It is the primary bridge between the security infrastructure
 * (JWT Encoders/Decoders) and the internal key management system.
 * </p>
 */
public interface PublicKeyResolver {

    /**
     * Retrieves a JWK currently eligible for creating new signatures.
     * <p>
     * This method is used by the {@code JwtEncoder} to identify which key should
     * be used for the next token. The selection logic may involve picking the
     * single {@code ACTIVE} key or load-balancing across multiple active keys.
     * </p>
     *
     * @return A {@link Jwk} with an {@code ACTIVE} status.
     * @throws NoActiveKeyAvailableException if no eligible key is found.
     */
    Jwk getActiveJwk();

    /**
     * Retrieves a specific JWK by its unique identifier for signature verification.
     * <p>
     * This is the "Verification Engine" entry point. It allows services like
     * {@code JwtDecoder} to find the exact public key that matches the
     * {@code kid} found in an incoming token's header.
     * </p>
     *
     * @param kid The unique Key ID.
     * @return The matching {@link Jwk} record.
     * @throws KeyNotFoundException if the kid is unrecognized.
     */
    Jwk getJwk(String kid);
}