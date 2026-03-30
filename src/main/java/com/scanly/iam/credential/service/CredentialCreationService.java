package com.scanly.iam.credential.service;

import com.scanly.iam.credential.persistence.UserCredentialRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Orchestrates the secure creation and storage of user authentication secrets.
 * <p>
 * This service acts as the bridge between the raw security inputs and the
 * persistent storage. It ensures that every password is cryptographically
 * secured via a {@link PasswordHasher} before it ever touches the database layer.
 * <p>
 * By using a decoupled hashing interface, this service remains framework-agnostic
 * and highly testable, fulfilling the IAM module's architectural requirement for
 * clean separation of concerns.
 */
@Service
public class CredentialCreationService {

    /** The internal strategy used to transform plain-text passwords into secure hashes. */
    private final PasswordHasher passwordHasher;

    /** The data access object for the iam.user_credentials schema. */
    private final UserCredentialRepository userCredentialRepository;

    /**
     * Constructs the service with its required security and persistence dependencies.
     *
     * @param passwordHasher           The domain-specific hasher (e.g., backed by Spring Security).
     * @param userCredentialRepository The repository for managing credential records.
     */
    public CredentialCreationService(PasswordHasher passwordHasher, UserCredentialRepository userCredentialRepository) {
        this.passwordHasher = passwordHasher;
        this.userCredentialRepository = userCredentialRepository;
    }

    /**
     * Secures and persists a new credential record for a specific user.
     * <p>
     * This method executes "Hash-and-Store" workflow:
     * <ol>
     * <li>Converts the raw {@code password} into a one-way hash.</li>
     * <li>Performs a native SQL insertion into the credential table.</li>
     * </ol>
     *
     * @param userId   The unique identifier of the user (owner of the credential).
     * @param password The raw, plain-text password provided by the user.
     * @return {@code true} if the credential was successfully persisted;
     * {@code false} if the insertion failed to affect exactly one row.
     */
    public boolean create(UUID userId, String password) {
        // Step 1: Secure the password at the boundary
        String passwordHash = passwordHasher.hash(password);

        // Step 2: Persist the resulting hash
        int insertedRowCount = userCredentialRepository.insertUserCredential(
                userId, passwordHash
        );

        return insertedRowCount == 1;
    }
}