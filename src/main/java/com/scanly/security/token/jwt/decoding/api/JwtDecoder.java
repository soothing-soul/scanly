package com.scanly.security.token.jwt.decoding.api;

import com.scanly.security.token.jwt.exception.JwtException;
import com.scanly.security.token.jwt.exception.JwtVerificationException;
import com.scanly.security.token.jwt.model.Jwt;

/**
 * Defines the contract for validating and parsing encoded JWT strings.
 * <p>
 * Implementations are responsible for:
 * <ul>
 * <li>Verifying the cryptographic signature using the public key.</li>
 * <li>Ensuring the presence of mandatory claims and the values thereof.</li>
 * <li>Mapping the verified claims into the Scanly {@link Jwt} record.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Implementation must also ensure that an instance of {@link JwtException}
 * should be thrown for any kind of failure so that downstrem services
 * could reliably handle the errors without relying on the underlying implementation
 * detail.
 * </p>
 */
public interface JwtDecoder {
    /**
     * Decodes and validates a raw JWT string.
     *
     * @param token The raw Base64URL-encoded JWT (header.payload.signature).
     * @return A verified {@link Jwt} record.
     * @throws JwtVerificationException if the token is invalid or expired.
     */
    Jwt decode(String token);
}