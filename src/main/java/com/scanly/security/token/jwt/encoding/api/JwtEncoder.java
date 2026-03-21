package com.scanly.security.token.jwt.encoding.api;

import com.scanly.security.token.jwt.encoding.model.JwtPayload;
import com.scanly.security.token.jwt.exception.JwtException;
import com.scanly.security.token.jwt.model.Jwt;

/**
 * Defines the contract for generating signed JSON Web Tokens (JWTs).
 * <p>
 * This interface abstracts the complexity of cryptographic signing and
 * claims serialization. Implementations are responsible for:
 * <ul>
 * <li>Generating the JwtHeader with correct kid and alg.</li>
 * <li>Applying the appropriate JWS algorithm (e.g., ES256).</li>
 * <li>Adding the mandatory claims (e.g., 'iss', 'iat').</li>
 * <li>Interacting with the Crypto package to sign the token header and payload.</li>
 * <li>Returning a fully-populated {@link Jwt} record.</li>
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
public interface JwtEncoder {

    /**
     * Encodes the provided payload into a signed JWT string and returns a structured model.
     * <p>
     * The returned {@link Jwt} instance contains both the raw encoded string
     * (the 'token' field) and the metadata (iat, exp, jti) that the calling
     * service may need for immediate processing or logging.
     * </p>
     *
     * <p>
     * Implementation is responsible for ensuring that the expiry value passed in the
     * payload doesn't exceed the configured max expiry value allowed for security reasons.
     * </p>
     *
     * @param payload The structured data, claims, and timing information
     * to be included in the JWT.
     * @return A {@link Jwt} record representing the newly created and signed token.
     */
    Jwt encode(JwtPayload payload);
}