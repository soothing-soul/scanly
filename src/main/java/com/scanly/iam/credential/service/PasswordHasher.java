package com.scanly.iam.credential.service;

/**
 * Domain-specific contract for password security operations.
 * <p>
 * This interface defines the essential cryptographic behaviors required by the
 * IAM module: transforming sensitive plain-text into secure hashes and
 * verifying those hashes later.
 * <p>
 * By using this abstraction, the {@link CredentialCreationService} remains agnostic of
 * the underlying hashing algorithm (e.g., BCrypt, Argon2) or the framework
 * providing it (e.g., Spring Security).
 */
public interface PasswordHasher {

    /**
     * Transforms a raw password into a secure, one-way cryptographic hash.
     * <p>
     * Implementation note: This should incorporate a unique salt for every
     * password to protect against rainbow table attacks.
     *
     * @param password The plain-text password to be secured.
     * @return A secure string representation of the password hash.
     */
    String hash(String password);

    /**
     * Verifies whether a raw password attempt aligns with a stored hash.
     * <p>
     * Because hashing is a one-way process, this method typically re-hashes
     * the provided {@code password} using the salt extracted from the {@code hash}
     * and performs a constant-time comparison of the results.
     *
     * @param password The plain-text attempt provided by the user.
     * @param hash     The previously stored hash to validate against.
     * @return {@code true} if the password is correct; {@code false} otherwise.
     */
    boolean match(String password, String hash);
}