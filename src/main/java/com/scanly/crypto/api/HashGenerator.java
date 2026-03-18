package com.scanly.crypto.api;

import com.scanly.crypto.exception.CryptoException;

/**
 * High-level utility for generating cryptographic digests (hashes).
 * <p>
 * This interface abstracts the process of creating fixed-size "fingerprints"
 * of arbitrary input data. It is intended for non-reversible operations
 * such as integrity checks, data indexing, and preparing payloads for
 * digital signatures.
 * </p>
 * <p>
 * <b>Note:</b> This is distinct from password hashing. For securing user
 * credentials, use a specialized password hashing utility that incorporates
 * salting and computational cost (like BCrypt).
 * </p>
 */
public interface HashGenerator {

    /**
     * Generates a cryptographic hash of the provided raw input.
     * <p>
     * Implementation should typically utilize collision-resistant algorithms
     * such as SHA-256 or SHA-512.
     * </p>
     *
     * @param input The raw byte array to be hashed.
     * @return The resulting message digest as a byte array.
     * @throws CryptoException if the underlying
     * hashing provider is unavailable or fails.
     */
    byte[] generateHash(byte[] input);
}