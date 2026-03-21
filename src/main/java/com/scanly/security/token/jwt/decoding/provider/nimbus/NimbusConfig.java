package com.scanly.security.token.jwt.decoding.provider.nimbus;

import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.scanly.security.token.jwt.config.JwtProperties;
import com.scanly.security.token.jwt.constants.JwtConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.HashSet;
import java.util.List;

/**
 * Spring Configuration for the JWT decoding infrastructure.
 * <p>
 * This class is intended to generate custom bean definitions for the classes
 * that belong to the nimbus APIs to override the default behavior.
 * </p>
 */
@Configuration
class NimbusConfig {
    /**
     * <p>
     * Generates a custom claims verifier that ensures the following:
     * <ul>
     *     <li>Mandatory claims are present</li>
     *     <li>Mandatory claims like {@link JwtConstants#ISSUER} with exact value
     *     requirement are enforced</li>
     *     <li>{@link JwtConstants#EXPIRES_AT} is valid.</li>
     * </ul>
     * </p>
     * @param jwtProperties
     * @return
     */
    @Bean
    public JWTClaimsSetVerifier<SecurityContext> jwtClaimsSetVerifier(JwtProperties jwtProperties) {
        List<String> mandatoryClaims = jwtProperties.getMandatoryClaims();
        JWTClaimsSet.Builder exactClaimsBuilder = new JWTClaimsSet.Builder();

        if (mandatoryClaims.contains(JwtConstants.ISSUER)) {
            exactClaimsBuilder = exactClaimsBuilder.issuer(jwtProperties.getIssuer());
        }

        DefaultJWTClaimsVerifier<SecurityContext> verifier = new DefaultJWTClaimsVerifier<>(
                exactClaimsBuilder.build(),
                new HashSet<>(jwtProperties.getMandatoryClaims())
        );
        return verifier;
    }

    /**
     * Generates an instance of NimbusJwtDecoder by plugging in the custom
     * implementation of {@link JWSKeySelector} and {@link JWTClaimsSetVerifier}.
     * @param keySelector to select the public key from the given JWSHeader
     * @param claimsVerifier to verify the claims as per our security policy
     * @return An instance of {@link NimbusJwtDecoder}
     */
    @Bean
    public JwtDecoder jwtDecoder(
            JWSKeySelector<SecurityContext> keySelector,
            JWTClaimsSetVerifier<SecurityContext> claimsVerifier
    ) {
        // The processor handles the heavy lifting of parsing the raw string
        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        // We plug in our custom selector to allow dynamic key lookups based on the JWS header
        jwtProcessor.setJWSKeySelector(keySelector);

        // We plug in our custom claims verifier to enforce our platform's security policy
        jwtProcessor.setJWTClaimsSetVerifier(claimsVerifier);

        // Finally, we wrap it in Spring's JwtDecoder interface for integration with the security filter chain
        return new NimbusJwtDecoder(jwtProcessor);
    }
}