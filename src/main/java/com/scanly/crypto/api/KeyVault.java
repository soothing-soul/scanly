package com.scanly.crypto.api;

import com.scanly.crypto.model.Jwk;
import com.scanly.crypto.model.KeyPairContainer;

import java.util.List;

/**
 * Acts as the low-level repository for cryptographic key material within the Scanly platform.
 * <p>
 * The {@code KeyVault} is a specialized storage abstraction. Its primary responsibility
 * is the persistence and retrieval of {@link KeyPairContainer} and {@link Jwk} objects.
 * As a "dumb storage" layer, it does not enforce lifecycle rules or business logic;
 * it simply executes CRUD operations.
 * </p>
 */
public interface KeyVault {

    /**
     * Retrieves a list of all Public-facing key representations currently in the vault.
     * <p>
     * Typically used by management services to audit the current key set or
     * by KeyResolver to find the key for signature and verification.
     * </p>
     * @return A list of {@link Jwk} objects.
     */
    List<Jwk> getAllJwks();

    /**
     * Persists or updates the collection of key pairs within the vault.
     * <p>
     * This is an "overwrite-style" operation used during initial bootstrap
     * or after the {@code KeyLifecycleManager} has calculated a new state for
     * the key ecosystem.
     * </p>
     * @param keys The authoritative list of {@link KeyPairContainer} objects to store.
     */
    void setKeyPairs(List<KeyPairContainer> keys);

    /**
     * Locates a public-facing JWK by its unique identifier.
     * <p>
     * Primarily used during token verification flows where only the public key
     * is required to validate a signature.
     * </p>
     * @param kid The unique Key ID.
     * @return The matching {@link Jwk}.
     * @throws com.scanly.crypto.exception.KeyNotFoundException if the ID does not exist.
     */
    Jwk findJwkByKid(String kid);

    /**
     * Retrieves the full internal container (including the Private Key) for a given ID.
     * <p>
     * <b>Security Note:</b> This method should be used exclusively by internal
     * signing services. It provides access to sensitive private material.
     * </p>
     * @param kid The unique Key ID.
     * @return The matching {@link KeyPairContainer}.
     * @throws com.scanly.crypto.exception.KeyNotFoundException if the ID does not exist.
     */
    KeyPairContainer findByKid(String kid);
}