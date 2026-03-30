package com.scanly.iam.credential.provider;

import com.scanly.iam.credential.service.PasswordHasher;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * A Spring Security-backed implementation of the {@link PasswordHasher} interface.
 * <p>
 * This class utilizes the {@link PasswordEncoderFactories#createDelegatingPasswordEncoder()}
 * to provide a "Self-Describing" hashing strategy. It automatically handles
 * salting, multiple hashing iterations, and algorithm versioning (e.g., BCrypt, Argon2).
 * <p>
 * This implementation ensures that the IAM core remains clean of Spring Security
 * imports while still benefiting from its battle-tested cryptographic logic.
 */
@Component
public class SpringSecurityPasswordHasher implements PasswordHasher {

    /**
     * The underlying Spring Security engine that performs the heavy lifting.
     * It defaults to BCrypt but can verify older or newer algorithms based
     * on the hash prefix (e.g., {bcrypt}, {argon2}).
     */
    private final PasswordEncoder passwordEncoder
            = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    /**
     * Hashes a raw password into a secure format prefixed with the algorithm ID.
     * <p>
     * Example output: {@code {bcrypt}$2a$10$vI8..XkS...}
     *
     * @param password The plain-text password to secure.
     * @return A secure, salted, and prefixed hash string.
     */
    @Override
    public String hash(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Verifies a raw password against a stored, self-describing hash.
     * <p>
     * The method identifies the algorithm to use by parsing the prefix
     * in the {@code passwordHash} string before performing the comparison.
     *
     * @param password     The raw password attempt.
     * @param passwordHash The stored hash (including algorithm prefix and salt).
     * @return {@code true} if the password matches; {@code false} otherwise.
     */
    @Override
    public boolean match(String password, String passwordHash) {
        return passwordEncoder.matches(password, passwordHash);
    }
}