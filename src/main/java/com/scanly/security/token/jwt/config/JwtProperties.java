package com.scanly.security.token.jwt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

/**
 * Configuration properties for JWT (JSON Web Token) management.
 * <p>
 * This class maps properties prefixed with {@code jwt} from the application
 * configuration files (e.g., application.yml) into a type-safe object.
 * These settings define the "Security Policy" for token issuance and validation
 * across the Scanly platform.
 */
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * The unique identifier of the principal that issued the JWT.
     * During validation, the 'iss' claim in the token must match this value exactly.
     */
    private String issuer;

    /**
     * The standard lifespan for a new token if no specific expiry is requested
     * during the encoding process.
     */
    private Duration defaultTimeToLive;

    /**
     * The absolute maximum allowed lifespan for any token.
     * This acts as a security ceiling to prevent the accidental issuance of
     * "immortal" or excessively long-lived tokens.
     */
    private Duration maxTimeToLive;

    /**
     * A list of claim keys that MUST be present in the JWT payload for it
     * to be considered valid (e.g., "sub", "jti", "exp").
     */
    private List<String> mandatoryClaims;

    /**
     * A list of algorithms that is supported for signature and verification
     */
    private List<String> supportedAlgorithms;

    /**
     * @return The list of algorithms supported by jwt package
     */
    public List<String> getSupportedAlgorithms() {
        return supportedAlgorithms;
    }

    /**
     * @param supportedAlgorithms List of algorithms to be supported
     */
    public void setSupportedAlgorithms(List<String> supportedAlgorithms) {
        this.supportedAlgorithms = supportedAlgorithms;
    }

    /**
     * @return The list of claim names required for a valid token.
     */
    public List<String> getMandatoryClaims() {
        return mandatoryClaims;
    }

    /**
     * @param mandatoryClaims The list of claim names to enforce during validation.
     */
    public void setMandatoryClaims(List<String> mandatoryClaims) {
        this.mandatoryClaims = mandatoryClaims;
    }

    /**
     * @return The platform's official issuer URI.
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * @param issuer The URI representing the trusted token issuer.
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * @return The default duration for token validity.
     */
    public Duration getDefaultTimeToLive() {
        return defaultTimeToLive;
    }

    /**
     * @param defaultTimeToLive The duration used when no specific expiry is provided.
     */
    public void setDefaultTimeToLive(Duration defaultTimeToLive) {
        this.defaultTimeToLive = defaultTimeToLive;
    }

    /**
     * @return The maximum allowed duration for any token.
     */
    public Duration getMaxTimeToLive() {
        return maxTimeToLive;
    }

    /**
     * @param maxTimeToLive The ceiling for token lifespan to mitigate risk of compromised keys.
     */
    public void setMaxTimeToLive(Duration maxTimeToLive) {
        this.maxTimeToLive = maxTimeToLive;
    }
}