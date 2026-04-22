package com.scanly.iam.auth.identification;

import jakarta.validation.constraints.Email;

/**
 * The initial request DTO used to start the authentication process.
 * <p>
 * This request captures the user's primary identifier (email) to begin
 * identity resolution. Upon receiving this, the system typically checks
 * if the user exists and determines the next required authentication step
 * (e.g., redirecting to a password prompt or an MFA challenge).
 * </p>
 *
 * @param email The email address of the user attempting to log in.
 * Must be a syntactically valid email format.
 */
public record AuthInitiateRequest(
        @Email(message = "Please provide a valid email address")
        String email
) {}