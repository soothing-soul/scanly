package com.scanly.iam.registration.web;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) returned upon a successful user registration.
 * <p>
 * This record provides the client with the essential metadata of the newly
 * created account. It confirms that all validation policies were satisfied
 * and that the user's identity and credentials have been persisted.
 * </p>
 *
 * @param userId The unique, system-generated {@link UUID} assigned to the
 * new user account.
 * @param email  The registered email address associated with the account.
 */
public record RegistrationResponse(
        UUID userId,
        String email
) {}
