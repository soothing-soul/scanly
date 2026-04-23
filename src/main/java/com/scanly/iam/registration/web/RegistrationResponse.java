package com.scanly.iam.registration.web;

import java.time.Instant;

/**
 * Data Transfer Object (DTO) returned upon a successful user registration.
 * <p>
 * This record provides the client with the credentials required to use the
 * protected resources of the application. It confirms that all validation
 * policies were satisfied and that the user's identity and credentials have
 * been persisted.
 * </p>
 *
 * @param accessToken The short-lived token that can be used to access the protected
 *                    resources of the application
 * @param expiresAt  Time after which the access token will not be valid
 */
public record RegistrationResponse(
        String accessToken,
        Instant expiresAt
) {}
