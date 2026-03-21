package com.scanly.security.token.jwt.encoding.provider.nimbus;

import com.nimbusds.jose.*;
import com.scanly.crypto.api.PublicKeyResolver;
import com.scanly.crypto.model.Jwk;
import com.scanly.security.token.jwt.config.JwtProperties;
import com.scanly.security.token.jwt.constants.JwtConstants;
import com.scanly.security.token.jwt.encoding.api.JwtEncoder;
import com.scanly.security.token.jwt.encoding.model.JwtPayload;
import com.scanly.security.token.jwt.exception.ExpiryLimitExceededException;
import com.scanly.security.token.jwt.exception.JwtSigningException;
import com.scanly.security.token.jwt.model.Jwt;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The Nimbus based implementation of the {@link JwtEncoder} interface.
 * <p>
 * This component is responsible for genreating the {@link Jwt} from the given
 * payload.
 * It leverages the Nimbus APIs to generate the header, and payload for the jwt,
 * while using {@link JWSSigner} to generate the signature.
 * </p>
 *
 * <p>
 * As per the contract of the {@link JwtEncoder}, it also ensures the presence
 * of mandatory claims and the permissible values thereof.
 * </p>
 */
@Component
class NimbusJwtEncoderAdapter implements JwtEncoder {
    /** The signer that bridges to the internal Scanly crypto package. */
    private final JWSSigner jwsSigner;

    /** Resolver used to identify the current active key for the 'kid' header. */
    private final PublicKeyResolver publicKeyResolver;

    /** Access the config properties related to Jwt */
    private final JwtProperties jwtProperties;

    public NimbusJwtEncoderAdapter(JWSSigner jwsSigner, PublicKeyResolver publicKeyResolver, JwtProperties jwtProperties) {
        this.jwsSigner = jwsSigner;
        this.publicKeyResolver = publicKeyResolver;
        this.jwtProperties = jwtProperties;
    }

    /**
     * Orchestrates the full JWT creation lifecycle: Header generation, Payload
     * assembly, Signing, and Model mapping.
     *
     * @param payload The raw data and configuration for the token.
     * @return A fully populated {@link Jwt} domain record.
     */
    @Override
    public Jwt encode(JwtPayload payload) {
        try {
            JWSHeader jwsHeader = generateJwsHeader();
            Payload jwsPayload = generatePayload(payload);
            JWSObject jwsObject = generateJwsObject(jwsHeader, jwsPayload);
            sign(jwsObject);
            Jwt jwt = transformToDomainJwt(jwsObject);
            return jwt;
        } catch (RuntimeException e) {
            throw new JwtSigningException(
                    "Failed to encode JWT payload.", e
            );
        }
    }

    /**
     * Constructs the JOSE Header.
     * Automatically retrieves the 'kid' from the active key in the crypto store.
     */
    private JWSHeader generateJwsHeader() {
        Jwk jwk = publicKeyResolver.getActiveJwk();
        String kid = jwk.kid();
        JWSAlgorithm signatureAlgorithm = getSignatureAlgorithm();
        return new JWSHeader.Builder(signatureAlgorithm)
                .keyID(kid)
                .type(JOSEObjectType.JWT)
                .build();
    }

    /**
     * Queries the JWSSigner for the supported algorithm. For all practical purpose,
     * this is going to remain a constant.
     * @return JWSAlgorithm
     */
    private JWSAlgorithm getSignatureAlgorithm() {
        return jwsSigner.supportedJWSAlgorithms().iterator().next();
    }

    /**
     * Builds the JWT Claims Set.
     * <p>
     * This method ensures the presence of mandatory claims as per the JwtProperties.
     * </p>
     */
    private Payload generatePayload(JwtPayload payload) {
        Map<String, Object> claims = new HashMap<>(payload.claims());

        Instant now = Instant.now();
        if (isClaimMandatory(JwtConstants.ISSUER)) {
            claims.put(JwtConstants.ISSUER, jwtProperties.getIssuer());
        }

        if (isClaimMandatory(JwtConstants.ISSUED_AT)) {
            claims.put(JwtConstants.ISSUED_AT, now.getEpochSecond());
        }

        if (isClaimMandatory(JwtConstants.EXPIRES_AT)) {
            Duration timeToLive = getTimeToLive(payload.timeToLive());
            claims.put(JwtConstants.EXPIRES_AT, now.plus(timeToLive).getEpochSecond());
        }

        if (isClaimMandatory(JwtConstants.JWT_ID)) {
            claims.computeIfAbsent(JwtConstants.JWT_ID, k -> getUniqueId());
        }

        return new Payload(claims);
    }

    /**
     * Checks whether the given claim is mandatory as per the JwtConfiguration or not.
     */
    private boolean isClaimMandatory(String claimName) {
        return jwtProperties.getMandatoryClaims().contains(claimName);
    }

    /**
     * Calculates the expiry value of the Jwt based on the requested expiry and the
     * default time to live as per the Jwt configuration.
     *
     * <p>
     * It also ensures that under no circumstance, the value of expiry will exceed the
     * prescribed limit as per the configured properties.
     * </p>
     * @param timeToLive requested expiry duration
     * @return actual expiry duration
     */
    private Duration getTimeToLive(Duration timeToLive) {
        if (timeToLive == null) {
            timeToLive = jwtProperties.getDefaultTimeToLive();
        }
        Duration maxAllowedExpiryDuration = jwtProperties.getMaxTimeToLive();
        if (timeToLive.compareTo(maxAllowedExpiryDuration) > 0) {
            throw new ExpiryLimitExceededException(
                    String.format("Requested expiry %s exceeds the maximum permissible limit", timeToLive)
            );
        }
        return timeToLive;
    }

    /**
     * Generates a collision-resistant unique identifier (jti).
     * <p>
     * It transforms a 128-bit UUID into a Base62 string to provide a compact,
     * URL-safe identifier for token tracking and revocation.
     * </p>
     */
    private String getUniqueId() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());

        BigInteger number = new BigInteger(1, buffer.array());
        StringBuilder stringBuilder = new StringBuilder();

        int base = 62;
        String alphabets = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divisionResult = number.divideAndRemainder(BigInteger.valueOf(base));
            number = divisionResult[0];
            stringBuilder.append(alphabets.charAt(divisionResult[1].intValue()));
        }
        return stringBuilder.isEmpty() ? "0" : stringBuilder.reverse().toString();
    }

    /**
     * Generate the JWSObject from the given header and payload
     * @param jwsHeader
     * @param payload
     * @return JWSObject
     */
    private JWSObject generateJwsObject(JWSHeader jwsHeader, Payload payload) {
        return new JWSObject(jwsHeader, payload);
    }

    /**
     * Executes the cryptographic signing of the JWS object.
     * @throws JwtSigningException if the underlying crypto machine fails.
     */
    private void sign(JWSObject jwsObject) {
        try {
            jwsObject.sign(jwsSigner);
        } catch (JOSEException e) {
            throw new JwtSigningException(
                    "Failed to sign JWS object", e
            ).withContext("payload", jwsObject.getPayload().toJSONObject());
        }
    }

    /**
     * Gets the token value from the signed JWSObject
     * @param jwsObject
     * @return tokenValue in the standard format {header.payload.signature}
     */
    private String getTokenValue(JWSObject jwsObject) {
        return jwsObject.serialize();
    }

    /**
     * Maps the final, signed JWSObject back into the Scanly {@link Jwt} record.
     */
    private Jwt transformToDomainJwt(JWSObject jwsObject) {
        // Extract headers and claims in the Map
        Map<String, Object> headers = jwsObject.getHeader().toJSONObject();
        Map<String, Object> claims = jwsObject.getPayload().toJSONObject();

        // Extract the relevant field for Jwt
        String jti = claims.get(JwtConstants.JWT_ID).toString();
        Instant iat = Instant.ofEpochSecond(((Number) claims.get(JwtConstants.ISSUED_AT)).longValue());
        Instant exp = Instant.ofEpochSecond(((Number) claims.get(JwtConstants.EXPIRES_AT)).longValue());

        // Get the token for the JWSObject
        String token = getTokenValue(jwsObject);

        return new Jwt(
                iat,
                exp,
                jti,
                headers,
                claims,
                token
        );
    }
}

