package com.scanly.security.token.jwt.decoding.provider.nimbus;

import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.scanly.security.token.jwt.constants.JwtConstants;
import com.scanly.security.token.jwt.decoding.api.JwtDecoder;
import com.scanly.security.token.jwt.exception.JwtVerificationException;
import com.scanly.security.token.jwt.model.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

/**
 * Standard implementation of the {@link JwtDecoder} interface for the Scanly platform.
 *
 * <p>
 * This class acts as a bridge between the low-level Nimbus JOSE library and the
 * scanly specific {@link JwtDecoder}.
 *
 * It leverages the {@link org.springframework.security.oauth2.jwt.NimbusJwtDecoder}
 * plugged in with custom {@link JWSKeySelector} and {@link JWTClaimsSetVerifier} to
 * perform the decoding from standard jwt format of (header.payload.signature).
 *
 * As an adapter, this is responsible for transforming the final response or any
 * library specific exception to the scanly specific response and exception.
 * </p>
 *
 * <p>
 * <b>Design Note:</b> This decoder is intentionally "stateless" regarding
 * business logic. It focuses strictly on structural integrity, cryptographic
 * authenticity, temporal validity (expiry/nbf), and mandatory claims enforced
 * at the infra level.
 * </p>
 */
@Component
class NimbusJwtDecoderAdapter implements JwtDecoder {
    /**
     * The internal delegate responsible for the actual decoding logic.
     */
    private final org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    public NimbusJwtDecoderAdapter(org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Decodes the JWT from its compact string representation into a {@link Jwt} object.
     * <p>
     * This method intercepts standard {@link JwtException}s and wraps them in a
     * domain-specific {@link JwtVerificationException} to provide better error
     * context for the Scanly platform's internal logging.
     * </p>
     *
     * @param token The raw, compact-format JWT string (header.payload.signature).
     * @return A successfully validated {@link Jwt} object.
     * @throws JwtVerificationException If the signature is invalid, the token is expired,
     * or mandatory claims are missing.
     */
    @Override
    public Jwt decode(String token) {
        try {
            return transformToDomainJwt(jwtDecoder.decode(token));
        } catch (JwtException e) {
            // Re-throwing as a domain-specific exception for consistent error handling across the app.
            throw new JwtVerificationException(
                    String.format("Verification failed for the token"),
                    e
            );
        }
    }

    /**
     * Transforms the response of {@link NimbusJwtDecoder} to the domain level
     * {@link Jwt}
     * @param jwt an instance of {@link org.springframework.security.oauth2.jwt.Jwt}
     * @return transformed value in {@link Jwt} format
     */
    private Jwt transformToDomainJwt(org.springframework.security.oauth2.jwt.Jwt jwt) {
        Instant issuedAt = jwt.getIssuedAt();
        Instant expiresAt = jwt.getExpiresAt();

        // Extract standard claims using platform constants
        String jwtId = jwt.getClaimAsString(JwtConstants.JWT_ID);

        String token = jwt.getTokenValue();
        Map<String, Object> headers = jwt.getHeaders();
        Map<String, Object> claims = jwt.getClaims();

        return new Jwt(
                issuedAt,
                expiresAt,
                jwtId,
                headers,
                claims,
                token
        );
    }
}