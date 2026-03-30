package com.scanly.iam.credential.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Persistence entity for storing user security credentials.
 * <p>
 * This entity is strictly responsible for the storage of secret material (hashes)
 * and password metadata. By separating this from the primary {@code UserEntity},
 * we reduce the risk of accidental leakage during standard user profile queries.
 * </p>
 */
@Entity
@Table(name = "user_credentials", schema = "iam")
public class UserCredentialEntity {

    /**
     * Primary key for the credential record.
     * <p>
     * This shares the same value as the {@code User} identity, establishing a
     * <b>one-to-one</b> relationship between the identity and its credentials.
     * </p>
     */
    @Id
    @Column(nullable = false, updatable = false)
    private UUID userId;

    /**
     * A salted and hashed representation of the user's password.
     * <b>Warning:</b> Never store raw passwords in this field.
     */
    @Column(nullable = false)
    private String passwordHash;

    /**
     * Audit timestamp indicating when the credential record was first created.
     */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Timestamp of the most recent password change.
     * <p>
     * This is essential for enforcing <b>password rotation policies</b> and
     * invalidating old sessions or tokens after a security update.
     * </p>
     */
    @Column(nullable = false)
    private Instant passwordUpdatedAt;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getPasswordUpdatedAt() {
        return passwordUpdatedAt;
    }

    public void setPasswordUpdatedAt(Instant passwordUpdatedAt) {
        this.passwordUpdatedAt = passwordUpdatedAt;
    }
}