package com.scanly.iam.policy.email.model;

import com.scanly.iam.policy.email.EmailPolicy;

/**
 * Represents the contextual data required to perform email policy validation.
 * <p>
 * This record serves as the primary input for {@link EmailPolicy} implementations.
 * It encapsulates all necessary information about an email-related operation (such
 * as registration, invitation, or update) to determine if it complies with defined
 * business and security rules.
 * </p>
 *
 * @param email The raw email address string to be validated (e.g., "user@example.com").
 */
public record EmailContext(
        String email
) {}
