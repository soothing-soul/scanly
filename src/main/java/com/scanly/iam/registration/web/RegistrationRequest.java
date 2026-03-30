package com.scanly.iam.registration.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) representing a user's registration request.
 * <p>
 * This record captures the initial credentials provided by a prospective user.
 * It uses JSR-303 validation annotations to ensure basic data integrity before
 * the request reaches the service layer.
 *
 * @param email    The unique email address for the new account. Validated for
 * standard email format and non-emptiness.
 * @param password The plain-text password chosen by the user. Note: This field
 * is encoded by the {@code CredentialCreationService} before persistence.
 */
public record RegistrationRequest(

        @Email(message = "Must be a valid email")
        @NotBlank(message = "Email must not be empty")
        String email,

        @NotBlank(message = "Password must not be empty")
        String password
) {}