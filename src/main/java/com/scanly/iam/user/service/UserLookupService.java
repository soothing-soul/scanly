package com.scanly.iam.user.service;

import com.scanly.iam.user.domain.User;
import com.scanly.iam.user.persistence.UserEntity;
import com.scanly.iam.user.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service responsible for retrieving user information from the persistence layer.
 * <p>
 * This service acts as a bridge between the database-centric {@link UserEntity}
 * and the domain-centric {@link User} model, ensuring that data access logic
 * remains decoupled from the core business logic.
 * </p>
 */
@Service
public class UserLookupService {

    private final UserRepository userRepository;

    /**
     * Constructs a new UserLookupService with the required repository.
     * @param userRepository the repository used for database operations
     */
    public UserLookupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a user by their unique email address.
     * @param email the email address to search for
     * @return an {@link Optional} containing the domain {@link User} if found,
     * otherwise an empty Optional
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::toUser);
    }

    /**
     * Retrieves a user by their unique identifier (UUID).
     * @param userId the UUID of the user
     * @return an {@link Optional} containing the domain {@link User} if found,
     * otherwise an empty Optional
     */
    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toUser);
    }

    /**
     * Maps a persistence {@link UserEntity} to a domain {@link User} object.
     * <p>
     * This internal mapping ensures that internal database details (like JPA annotations)
     * do not leak into the rest of the application.
     * @param userEntity the database entity to convert
     * @return a new domain User instance
     */
    private User toUser(UserEntity userEntity) {
        return new User(
                userEntity.getUserId(),
                userEntity.getEmail(),
                userEntity.isEmailVerified(),
                userEntity.getStatus()
        );
    }
}