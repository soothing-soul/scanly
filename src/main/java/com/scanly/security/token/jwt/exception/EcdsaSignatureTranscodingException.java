package com.scanly.security.token.jwt.exception;

/**
 * Exception thrown when the system fails to transcode an ECDSA signature between
 * the Java Security (DER/ASN.1) format and the JWS (R + S Concatenated) format.
 * <p>
 * This is a specialized infrastructure failure that occurs during the final
 * stages of JWT encoding or the initial stages of decoding. Common causes include:
 * <ul>
 * <li>Invalid signature length during R+S concatenation.</li>
 * <li>Malformations in the DER sequence during decoding.</li>
 * <li>Mathematical mismatches when extracting the R and S components from a raw signature.</li>
 * </ul>
 * </p>
 * This exception maps to {@link JwtError#ECDSA_SIGNATURE_TRANSCODE_ERROR} and
 * results in an Internal Server Error (500).
 */
public class EcdsaSignatureTranscodingException extends JwtException {

    /**
     * The fixed error category for signature format conversion failures.
     */
    private static final JwtError error = JwtError.ECDSA_SIGNATURE_TRANSCODE_ERROR;

    /**
     * Constructs a new transcoding exception with a detail message.
     *
     * @param message Explains the specific failure (e.g., "Invalid DER length").
     */
    public EcdsaSignatureTranscodingException(String message) {
        super(message, error);
    }

    /**
     * Constructs a new transcoding exception with a message and the underlying root cause.
     *
     * @param message Explains the failure context.
     * @param cause   The original exception (e.g., a {@code SignatureException}
     * or an {@code IOException}) that triggered the failure.
     */
    public EcdsaSignatureTranscodingException(String message, Throwable cause) {
        super(message, error, cause);
    }
}