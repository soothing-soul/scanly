package com.scanly.iam.user.persistence;

import com.scanly.iam.user.domain.UserStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * The JPA entity representing the persistent state of a user in the database.
 * * <p>This entity maps the {@code User} domain model to the {@code iam.users} table.
 * It includes auditing metadata ({@code createdAt}, {@code updatedAt}) that is
 * not typically part of the core domain logic but is essential for data persistence.
 */
@Entity
@Table(name = "users", schema = "iam")
public class UserEntity {
    /**
     * The primary key for the user record.
     * Maps to the {@code user_id} column.
     */
    @Id
    @Column(nullable = false, updatable = false)
    private UUID userId;

    /**
     * The unique email address of the user.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Flag indicating if the user's email has been verified.
     */
    @Column(nullable = false)
    private boolean emailVerified;

    /**
     * The current lifecycle status of the user account.
     * Stored as a string in the database for better readability and easier migrations.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    /**
     * The timestamp when the user record was first persisted.
     */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * The timestamp of the most recent update to the user record.
     */
    @Column(nullable = false)
    private Instant updatedAt;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
