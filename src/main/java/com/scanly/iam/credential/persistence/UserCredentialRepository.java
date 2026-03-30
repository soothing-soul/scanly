package com.scanly.iam.credential.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Data access interface for managing {@link UserCredentialEntity} records.
 * <p>
 * This repository is responsible for the secure storage and retrieval of
 * cryptographic password hashes. It operates within the {@code iam} database
 * schema to ensure credentials are kept separate from general user metadata.
 * </p>
 */
@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredentialEntity, UUID> {

    /**
     * Persists a new set of credentials for a specific user using a native SQL insert.
     * <p>
     * This method is marked with {@link Modifying} because it performs a DML
     * (Data Manipulation Language) operation. By using a native query, it avoids
     * the overhead of the JPA entity state transitions, which is beneficial
     * during high-concurrency registration or bulk migration flows.
     *
     * @param userId       The unique identifier of the user (Foreign Key to iam.users).
     * @param passwordHash The pre-computed cryptographic hash of the password.
     * @return The number of rows affected (typically 1).
     */
    @Modifying
    @Query(
            value =
            """
                INSERT INTO iam.user_credentials (user_id, password_hash)
                VALUES (:userId, :passwordHash)
            """,
            nativeQuery = true
    )
    int insertUserCredential(
            @Param("userId") UUID userId,
            @Param("passwordHash") String passwordHash
    );
}