package com.scanly.iam.policy.model;

/**
 * A structured representation of a single policy validation error.
 * <p>
 * This record encapsulates a unique error code and a human-readable message
 * describing why a specific policy requirement was not met. It is used in
 * {@link PolicyValidationResult} as well as in the final response to the client
 * by wrapping multiple errors all at once in a list.
 * </p>
 *
 * @param errorCode A machine-readable string (e.g., "EMAIL_DOMAIN_BLACKLISTED")
 * used for programmatic error handling or localization.
 * @param message   A human-readable explanation of the error (e.g., "The email
 * domain 'example.com' is not allowed.").
 */
public record PolicyValidationError(
        String errorCode,
        String message
) {}