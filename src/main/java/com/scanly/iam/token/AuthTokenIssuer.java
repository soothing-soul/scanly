package com.scanly.iam.token;

import com.scanly.iam.token.accesstoken.AccessToken;
import com.scanly.iam.token.accesstoken.AccessTokenGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * High-level orchestrator responsible for the issuance and lifecycle coordination
 * of user authentication credentials within the Scanly IAM domain.
 * <p>
 * This component implements the Facade pattern, providing a unified interface
 * for the security/auth layers to request a complete {@link AuthToken} bundle
 * without needing to understand the specific generation logic for individual
 * token types (Access, Refresh, etc.).
 * <p>
 * By centralizing token issuance here, the system maintains a clean separation
 * between specific token generation strategies and the overall authentication flow.
 */
@Component
public class AuthTokenIssuer {

    private final AccessTokenGenerator accessTokenGenerator;

    /**
     * Initializes the issuer with required generator dependencies.
     * @param accessTokenGenerator The specialized generator for short-lived access credentials.
     */
    public AuthTokenIssuer(AccessTokenGenerator accessTokenGenerator) {
        this.accessTokenGenerator = accessTokenGenerator;
    }

    /**
     * Performs the orchestration logic to issue a comprehensive authentication bundle
     * for a verified user identity.
     * <p>
     * This method acts as the integration point for all credentials associated with
     * a user session. As the system evolves to support token rotation, this method
     * will coordinate the concurrent generation of both access and refresh tokens.
     * </p>
     *
     * @param userId The {@link UUID} of the user for whom the tokens are being issued.
     * Must correspond to a valid subject in the system.
     * @return A fully populated {@link AuthToken} containing the necessary credentials
     * to access protected resources.
     * @see AccessTokenGenerator#generate(UUID)
     */
    public AuthToken issue(UUID userId) {
        AccessToken accessToken = accessTokenGenerator.generate(userId);

        return new AuthToken(accessToken);
    }
}