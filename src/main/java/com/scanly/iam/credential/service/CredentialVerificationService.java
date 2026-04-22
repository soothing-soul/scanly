package com.scanly.iam.credential.service;

import com.scanly.iam.auth.common.exception.UserNotFoundException;
import com.scanly.iam.credential.persistence.UserCredentialEntity;
import com.scanly.iam.credential.persistence.UserCredentialRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Domain service responsible for the secure verification of user credentials.
 * <p>
 * This service encapsulates the interaction with the credential repository and
 * the cryptographic matching logic. It is designed to be used by authentication
 * executors to validate a user's "Knowledge" factor (password) against the
 * system of record.
 * </p>
 */
@Service
public class CredentialVerificationService {

    private final PasswordHasher passwordHasher;
    private final UserCredentialRepository userCredentialRepository;

    /**
     * Constructs the CredentialVerificationService.
     *
     * @param passwordHasher           The abstraction for password hashing and matching.
     * @param userCredentialRepository The repository for accessing persisted user credentials.
     */
    public CredentialVerificationService(PasswordHasher passwordHasher,
                                         UserCredentialRepository userCredentialRepository) {
        this.passwordHasher = passwordHasher;
        this.userCredentialRepository = userCredentialRepository;
    }

    /**
     * Verifies a raw password against the stored hash for a specific user.
     * <p>
     * <b>Process:</b>
     * <ol>
     * <li>Look up the user's credential record by {@code userId}.</li>
     * <li>If found, use the {@link PasswordHasher} to cryptographically
     * compare the raw password with the stored hash.</li>
     * <li>If not found, throw an internal {@link UserNotFoundException}.</li>
     * </ol>
     * </p>
     *
     * @param userId   The unique identifier of the user to verify.
     * @param password The raw password provided during the authentication attempt.
     * @return {@code true} if the password matches the stored hash; {@code false} otherwise.
     * @throws UserNotFoundException if no credential record exists for the provided ID.
     */
    public boolean verify(UUID userId, String password) {
        // Retrieve the entity from the database
        UserCredentialEntity userCredential = userCredentialRepository.findById(userId)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                String.format("User not found for userId: %s", userId)
                        )
                );

        // Match the raw input against the cryptographic hash
        return passwordHasher.match(password, userCredential.getPasswordHash());
    }
}