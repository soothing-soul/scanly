package com.scanly.iam.user.service;

import com.scanly.iam.user.domain.User;
import com.scanly.iam.user.domain.UserStatus;
import com.scanly.iam.user.exception.EmailAlreadyExistsException;
import com.scanly.iam.user.persistence.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsible for the initial creation and persistence of user accounts.
 * <p>
 * This class abstracts the complexities of user insertion and error mapping.
 * It coordinates with the {@link UserRepository} to perform low-level database
 * operations and transforms persistence-layer exceptions into meaningful
 * business exceptions.
 */
@Service
public class UserCreationService {

    /** Repository for accessing the IAM user data store. */
    private final UserRepository userRepository;

    /**
     * Constructs the service with the required user repository.
     *
     * @param userRepository The data access component for user persistence.
     */
    public UserCreationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user record in the system with the provided email.
     * <p>
     * This method attempts to insert the user directly via a native query for
     * performance. If the email is already associated with an existing account,
     * a {@link DataIntegrityViolationException} is caught and rethrown as a
     * domain-specific {@link EmailAlreadyExistsException}.
     *
     * @param email The unique email address for the new user.
     * @return A {@link User} record representing the newly persisted identity.
     * @throws EmailAlreadyExistsException if the provided email violates the
     * uniqueness constraint in the database.
     * @throws DataIntegrityViolationException for any other integrity failures
     * (e.g., null violations) that are not explicitly handled.
     */
    public User create(String email) {
        UUID userId = null;
        try {
            // Execution of the optimized native insert
            userId = userRepository.insertUser(email);
        } catch (DataIntegrityViolationException dve) {
            // Mapping SQL Unique Constraint violations to Business Exceptions
            if (isConstraintViolation(dve, "uq_users_email")) {
                throw new EmailAlreadyExistsException(
                        String.format("Account already exists with email %s", email),
                        dve
                );
            }
            throw dve;
        }

        // Return the domain representation of the new user
        return new User(
                userId,
                email,
                false, // Default: account is not yet verified
                UserStatus.ACTIVE
        );
    }

    /**
     * Evaluates if a DataAccessException was triggered by a specific named database constraint.
     * <p>
     * This predicate avoids fragile string parsing by searching for the underlying
     * {@link ConstraintViolationException} (or JPA equivalent) and inspecting its
     * metadata.
     *
     * @param e The high-level Spring exception thrown by the repository.
     * @param constraintName The exact name of the database constraint to verify
     * (e.g., "uq_users_email").
     * @return {@code true} if the exception root cause is a violation of the
     * specified constraint; {@code false} otherwise.
     */
    private boolean isConstraintViolation(DataIntegrityViolationException e, String constraintName) {
        ConstraintViolationException cve =
                findCause(e, ConstraintViolationException.class);

        return cve != null && cve.getConstraintName().equalsIgnoreCase(constraintName);
    }

    /**
     * Traverses the exception "cause" chain to locate an instance of a specific exception type.
     * <p>
     * Since Spring's {@code DataAccessException} hierarchy often wraps root causes
     * (like JDBC or Hibernate exceptions) several layers deep, this utility unwraps
     * the stack until the requested {@link Throwable} type is found or the end
     * of the chain is reached.
     * @param <T>  The specific type of Throwable to search for.
     * @param e    The starting exception to unwrap.
     * @param type The Class object of the target exception type.
     * @return The found exception of type {@code T}, or {@code null} if no such
     * cause exists in the hierarchy.
     */
    private <T extends Throwable> T findCause(DataIntegrityViolationException e, Class<T> type) {
        Throwable cause = e;
        while (cause != null) {
            if (cause.getClass().equals(type)) {
                return (T) cause;
            }
            cause = cause.getCause();
        }
        return null;
    }
}