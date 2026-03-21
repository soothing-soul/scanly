package com.scanly.security.token.jwt.exception;

/**
 * Exception thrown when a failure occurs during the cryptographic signing of a JWT.
 * <p>
 * This exception specifically indicates a server-side failure during the JWS
 * (JSON Web Signature) creation process. Common causes include:
 * <ul>
 * <li>Inaccessible or corrupted Private Keys.</li>
 * <li>Misconfigured Signature algorithms (e.g., ES256).</li>
 * <li>Internal failures in the underlying JCA (Java Cryptography Architecture) providers.</li>
 * </ul>
 * </p>
 * This exception maps to {@link JwtError#SIGNING_FAILURE} and results in an
 * Internal Server Error (500) status.
 */
public class JwtSigningException extends JwtException {

    /**
     * The fixed error category for all signing-related failures.
     */
    private static final JwtError error = JwtError.SIGNING_FAILURE;

    /**
     * Constructs a new signing exception with a specific detail message.
     *
     * @param message The technical explanation of why the signing failed.
     */
    public JwtSigningException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new signing exception with a message and the underlying root cause.
     * <p>
     * Use this constructor to wrap low-level cryptographic exceptions (like
     * {@code SignatureException} or {@code JOSEException}) while maintaining
     * the original stack trace for debugging.
     * </p>
     *
     * @param message The technical explanation of the failure.
     * @param cause   The original exception that triggered the signing failure.
     */
    public JwtSigningException(String message, Throwable cause) {
        super(message, error, cause);
    }
}