package com.scanly.crypto.provider.common;

import com.scanly.crypto.api.HashGenerator;
import com.scanly.crypto.config.CryptoProperties;
import com.scanly.crypto.exception.AlgorithmNotFoundException;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A concrete implementation of {@link HashGenerator} utilizing the SHA-256 algorithm.
 * <p>
 * This component provides high-performance, collision-resistant cryptographic
 * hashing. It is suitable for integrity checks, data fingerprinting, and
 * preparing large payloads for digital signing.
 * </p>
 */
@Component
public class Sha256HashGenerator implements HashGenerator {
    private final CryptoProperties cryptoProperties;

    public Sha256HashGenerator(CryptoProperties cryptoProperties) {
        this.cryptoProperties = cryptoProperties;
    }

    /**
     * Generates a 256-bit (32-byte) hash for the provided input.
     * <p>
     * This operation is deterministic: the same input will always yield the
     * same byte array output.
     * </p>
     *
     * @param input The raw byte data to be processed.
     * @return A 32-byte array representing the SHA-256 digest.
     * @throws AlgorithmNotFoundException If the JVM does not support SHA-256
     * (indicates a severe environment configuration issue).
     */
    @Override
    public byte[] generateHash(byte[] input) {
        MessageDigest digest;
        String hashAlgorithm = cryptoProperties.getHashAlgorithm();
        try {
            // Initialize the JCA MessageDigest engine
            digest = MessageDigest.getInstance(hashAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is mandatory in Java environments; this is a critical failure.
            throw new AlgorithmNotFoundException(
                    String.format("Hashing Algorithm %s not found in the current JVM", hashAlgorithm), e
            );
        }
        return digest.digest(input);
    }
}