package com.scanly.iam.token.accesstoken;

import com.scanly.security.token.jwt.constants.JwtConstants;
import com.scanly.security.token.jwt.encoding.api.JwtEncoder;
import com.scanly.security.token.jwt.encoding.model.JwtPayload;
import com.scanly.security.token.jwt.model.Jwt;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A specialized domain service within the Identity and Access Management (IAM) module
 * dedicated to the production of short-lived Access Tokens.
 * <p>
 * This service serves as an adapter between the Scanly IAM domain and the core
 * Security infrastructure. It encapsulates the business rules for what data
 * (claims) should be embedded within a user's session token and delegates the
 * cryptographic heavy lifting to the {@link JwtEncoder}.
 * <p>
 * <b>Architectural Note:</b> This service is intentionally restricted to Access Token
 * generation only. It is designed to be utilized by an orchestrator that manages
 * the overall authentication response bundle.
 */
@Service
public class AccessTokenGenerator {

    /**
     * The fixed lifespan of an issued access token.
     * <p>
     * A 10-minute duration is enforced to balance user experience with security
     * best practices, ensuring that leaked tokens have a strictly limited
     * window of utility before expiration.
     * </p>
     */
    private final static Duration ACCESS_TOKEN_VALIDITY = Duration.ofMinutes(10);

    /**
     * Underlying {@code jwt} service used to generate the token
     */
    private final JwtEncoder jwtEncoder;

    /**
     * Constructs the generator with the required security infrastructure.
     *
     * @param jwtEncoder The infrastructure component responsible for signing
     * and serializing the JWT payload.
     */
    public AccessTokenGenerator(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    /**
     * Orchestrates the transformation of a User identity into a signed Access Token.
     * <p>
     * The process involves:
     * <ol>
     * <li>Assembling a domain-specific {@link JwtPayload} for the user.</li>
     * <li>Encoding and signing the payload via the security infrastructure.</li>
     * <li>Mapping the result into an IAM-compliant {@link AccessToken} DTO.</li>
     * </ol>
     *
     * @param userId The unique identifier of the subject requesting the token.
     * @return A signed {@link AccessToken} containing the raw token string and
     * its calculated expiration timestamp.
     */
    public AccessToken generate(UUID userId) {
        JwtPayload claims = generatePayload(userId);

        /*
         * Decoupling Point: We delegate the cryptographic encoding to the security module.
         * This ensures the IAM domain remains agnostic of the specific signing algorithms
         * (RSA, HMAC, etc.) and key management strategies configured in the system.
         */
        Jwt jwt = jwtEncoder.encode(claims);

        return new AccessToken(
                jwt.token(),
                jwt.exp()
        );
    }

    /**
     * Internal logic for defining the content of the Access Token's claims set.
     * <p>
     * This method maps user-specific attributes to the standard claims format required
     * by the JWT encoder. It acts as the "source of truth" for the identity and
     * authorization data carried by the token.
     * </p>
     *
     * @param userId The user ID to be set as a primary identity claim.
     * @return A {@link JwtPayload} configured with the default validity period and user claims.
     */
    private JwtPayload generatePayload(UUID userId) {
        Map<String, Object> claims = new HashMap<>();

        // Primary identity claim for identifying the subject across microservices
        claims.put(JwtConstants.SUBJECT, userId);

        /*
         * Authorization Context: Currently, all users are issued with a BASIC plan.
         * This is a placeholder for a future lookup service that will resolve
         * specific user roles and subscription tiers during the issuance flow.
         */
        claims.put("plan", "BASIC");

        return new JwtPayload(
                ACCESS_TOKEN_VALIDITY,
                claims
        );
    }
}