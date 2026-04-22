package com.scanly.iam.user.domain;

import java.util.UUID;

/**
 * The authoritative identity of a user within the Scanly platform.
 * <p>This record serves as the <b>Single Source of Truth (SSoT)</b> for user identity.
 * By centralizing these fields, the system ensures:
 * <ul>
 * <li>Global consistency of {@code userId} across all platform services.</li>
 * <li>Enforcement of email uniqueness and ownership.</li>
 * <li>Centralized control over the user's operational lifecycle and access rights.</li>
 * </ul>
 * @param userId        The immutable, globally unique identifier for the user.
 * @param email         The primary email address used for identification and communication.
 * @param emailVerified Indicates if the user has successfully completed the email verification process.
 * @param status        The current functional state of the user account (e.g., ACTIVE, SUSPENDED).
 */
public record User(
        UUID userId,
        String email,
        boolean emailVerified,
        UserStatus status
) {}