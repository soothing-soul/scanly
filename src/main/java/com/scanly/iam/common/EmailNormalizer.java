package com.scanly.iam.common;

/**
 * Utility class for standardizing email addresses across the IAM module.
 * <p>
 * Normalization ensures that " User@Example.com " and "user@example.com"
 * are treated as the exact same identity.
 * </p>
 */
public class EmailNormalizer {
    /**
     * Standardizes the provided email string by removing leading/trailing
     * whitespace and converting all characters to lowercase.
     *
     * @param email The raw email string received from a RequestBody or external source.
     * @return A trimmed, lowercase version of the email.
     * Returns null if the input is null.
     */
    public static String normalize(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
}