package com.scanly.crypto.model;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;

/**
 * A comprehensive container for an asymmetric key pair and its associated metadata.
 * <p>
 * This record serves as the internal representation of a cryptographic identity
 * within the Scanly platform. It bridges the gap between raw Java Security keys
 * and the platform's lifecycle management logic.
 * </p>
 * @param kid        The unique Key ID used to identify this pair in JWT headers.
 * @param alg        The specific {@link Algorithm} this key pair is intended for (e.g., ES256).
 * @param keyStatus  The current {@link KeyStatus} governing how these keys can be used.
 * @param publicKey  The public component used for signature verification.
 * @param privateKey The private component used for data signing. <b>Should be handled with extreme care.</b>
 * @param createdAt  The timestamp of when this key pair was generated or loaded.
 * @param expiresAt  The timestamp after which this key pair should transition to EXPIRED.
 */
public record KeyPairContainer(
        String kid,
        Algorithm alg,
        KeyStatus keyStatus,
        PublicKey publicKey,
        PrivateKey privateKey,
        Instant createdAt,
        Instant expiresAt
) {}