package com.scanly.common.hashing;

import com.scanly.crypto.api.HashGenerator;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/**
 * Service providing a high-level API for cryptographic hashing and verification.
 * <p>
 * This service acts as a bridge between the low-level {@link HashGenerator} and
 * the application domains, ensuring that all hashes are handled as hexadecimal
 * strings for consistent storage and transmission.
 * </p>
 * <p>
 * <b>Note:</b> All string-to-byte conversions strictly use {@code UTF-8} to
 * maintain character encoding consistency across different system environments.
 * </p>
 */
@Service
public class HashingService {

    /** The underlying engine responsible for the cryptographic hash generation. */
    private final HashGenerator hashGenerator;

    /**
     * Constructs the service with a specific hash generator implementation.
     *
     * @param hashGenerator The provider used to execute the underlying hash algorithm.
     */
    public HashingService(HashGenerator hashGenerator) {
        this.hashGenerator = hashGenerator;
    }

    /**
     * Transforms a raw input string into a hexadecimal hash string.
     * <p>
     * This method converts the input to a UTF-8 byte array, generates the
     * cryptographic hash, and then formats the resulting bytes into a
     * lowercase hex string.
     * </p>
     *
     * @param input The raw plain-text string to be hashed.
     * @return A hexadecimal representation of the resulting hash.
     */
    public String hash(String input) {
        byte[] hash = hashGenerator.generateHash(input.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }

    /**
     * Verifies that a raw input string matches a previously generated hexadecimal hash.
     * <p>
     * This is achieved by re-hashing the provided {@code input} and performing
     * a string comparison against the stored {@code hash}.
     * </p>
     *
     * @param hash  The existing hexadecimal hash to compare against.
     * @param input The raw input string to validate.
     * @return {@code true} if the generated hash of the input matches the provided hash.
     */
    public boolean verify(String hash, String input) {
        byte[] inputHashInBytes = hashGenerator.generateHash(input.getBytes(StandardCharsets.UTF_8));
        String inputHashInString = HexFormat.of().formatHex(inputHashInBytes);
        return inputHashInString.equals(hash);
    }
}