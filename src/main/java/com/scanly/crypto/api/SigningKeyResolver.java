package com.scanly.crypto.api;

import com.scanly.crypto.exception.NoActiveKeyAvailableException;
import com.scanly.crypto.model.Jwk;

/**
 * Strategy interface for resolving the metadata of the current signing key.
 * <p>
 * This interface is a core component of the "Fortress" security architecture,
 * providing the {@code JwtEncoder} with the necessary headers (kid, alg) to
 * create RFC-compliant tokens.
 * </p>
 * <p>
 * <b>Implementation Note:</b> Implementations should be thread-safe and capable
 * of handling key rotation logic internally.
 * </p>
 */
public interface SigningKeyResolver {

    /**
     * Resolves and returns the metadata (JWK) for the active signing key.
     * <p>
     * The returned {@link Jwk} object contains the Key ID (kid) and the
     * intended algorithm, which are mandatory for standard JWS headers.
     * </p>
     *
     * <p>
     * The implementation is also responsible to ensure only keys which are
     * currently eligible to perform signing operation are returned.
     * </p>
     *
     * @return The {@link Jwk} representing the metadata of the active key.
     * @throws NoActiveKeyAvailableException if the system cannot find a
     * valid key to perform signing operations.
     */
    Jwk getSigningKeyMetadata();
}
