package com.scanly.crypto.model;

/**
 * Enumerates the cryptographic algorithms supported by the Scanly platform.
 * <p>
 * This enum acts as a mapping layer between JWS (JSON Web Signature) standard
 * naming conventions and the underlying Java Cryptography Architecture (JCA)
 * identifiers.
 * </p>
 */
public enum Algorithm {
    /**
     * ECDSA using P-256 and SHA-256.
     * <p>
     * Recommended for its high security-to-key-length ratio and performance
     * in modern web environments.
     * </p>
     */
    ES256("EC", "ES256", "SHA256withECDSA");

    /** The cryptographic key family (e.g., "EC" for Elliptic Curve, "RSA"). */
    private final String keyFamily;

    /** The standardized JWS name (used in JWT headers). */
    private final String name;

    /** The specific JCA algorithm name used by {@link java.security.Signature}. */
    private final String signatureAlgorithm;

    Algorithm(String keyFamily, String name, String algorithm) {
        this.keyFamily = keyFamily;
        this.name = name;
        this.signatureAlgorithm = algorithm;
    }

    /**
     * @return The key factory identifier (e.g., "EC").
     */
    public String getKeyFamily() {
        return keyFamily;
    }

    /**
     * @return The JWS algorithm name (e.g., "ES256").
     */
    public String getName() {
        return name;
    }

    /**
     * @return The JCA-standard signature algorithm name.
     */
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }
}