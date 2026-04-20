package com.scanly.otp.service;

import java.security.SecureRandom;

/**
 * A utility class responsible for the cryptographically secure generation of
 * One-Time Passwords.
 * <p>
 * This class is intentionally package-private, as it is an internal implementation
 * detail of the {@code otp} service. It utilizes {@link SecureRandom} to ensure
 * that the generated codes are unpredictable and resistant to brute-force or
 * pattern-matching attacks.
 * </p>
 */
class OtpGenerator {

    /**
     * The standard length for generated OTPs.
     * A 6-digit code provides a balance between user convenience and security.
     */
    private static final int OTP_LENGTH = 6;

    /**
     * The source of randomness for OTP generation.
     * {@link SecureRandom} is used over {@code Random} to provide
     * cryptographically strong random number generation.
     */
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a numeric string of length {@value #OTP_LENGTH}.
     * <p>
     * The resulting string consists only of digits (0-9). This numeric-only
     * approach is optimized for ease of entry on mobile devices and
     * numeric keypads.
     * </p>
     *
     * @return A {@code String} containing the generated 6-digit OTP.
     */
    public static String generate() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}