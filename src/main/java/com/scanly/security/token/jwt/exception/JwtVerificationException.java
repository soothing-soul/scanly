package com.scanly.security.token.jwt.exception;

/**
 * Exception thrown when an incoming JWT fails the validation process.
 * <p>
 * This is the primary exception used by the decoding infrastructure to signal
 * that a token is untrustworthy. It encapsulates a variety of failure scenarios, including:
 * <ul>
 * <li><b>Signature Mismatch:</b> The token has been tampered with or signed by an unknown key.</li>
 * <li><b>Token Expiration:</b> The current time is after the 'exp' claim value.</li>
 * <li><b>Malformed Token:</b> The string does not adhere to the JWT (header.payload.signature) format.</li>
 * <li><b>Invalid Claims:</b> Mandatory claims (like 'iss', 'exp') are missing or incorrect.</li>
 * </ul>
 * </p>
 * This exception maps to {@link JwtError#VERIFICATION_FAILURE} and should typically
 * result in an <b>Unauthorized (401)</b> response to the client.
 */
public class JwtVerificationException extends JwtException {

    /**
     * The fixed error category for all token validation failures.
     */
    private static final JwtError error = JwtError.VERIFICATION_FAILURE;

    /**
     * Constructs a new verification exception with a descriptive message.
     *
     * @param message A developer-friendly explanation of why verification failed.
     */
    public JwtVerificationException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new verification exception by wrapping a lower-level failure.
     * <p>
     * Use this constructor to wrap exceptions from the underlying JOSE provider
     * (e.g., Spring's {@code BadJwtException} or Nimbus's {@code JOSEException}).
     * </p>
     *
     * @param message The technical context of the failure.
     * @param cause   The original exception that triggered the verification failure.
     */
    public JwtVerificationException(String message, Throwable cause) {
        super(message, error, cause);
    }
}