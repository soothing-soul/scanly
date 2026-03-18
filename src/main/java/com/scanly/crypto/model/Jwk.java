package com.scanly.crypto.model;

import java.security.PublicKey;
import java.time.Instant;

/**
 * A public representation of a JSON Web Key (JWK) within the Scanly platform.
 * <p>
 * This record serves as a "Public-Only" view of a key pair. It contains the
 * cryptographic material and metadata necessary for signature verification
 * and key lifecycle management, but intentionally excludes the Private Key.
 * </p>
 * @param kid        The unique Key ID used to identify this key in JWT headers.
 * @param alg        The {@link Algorithm} associated with this key (e.g., ES256).
 * @param keyStatus  The current {@link KeyStatus} (ACTIVE, RETIRING, etc.).
 * @param publicKey  The JCA {@link PublicKey} used to verify signatures.
 * @param createdAt  The timestamp of key generation.
 * @param expiresAt  The timestamp of scheduled decommissioning.
 */
public record Jwk(
        String kid,
        Algorithm alg,
        KeyStatus keyStatus,
        PublicKey publicKey,
        Instant createdAt,
        Instant expiresAt
) {}