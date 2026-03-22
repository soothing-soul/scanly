package com.scanly.crypto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Holds externalized configuration for cryptographic key locations.
 * <p>
 * This class allows the {@code KeyLifecycleManager} implementations to resolve the physical
 * location of public and private keys.
 * </p>
 */
@ConfigurationProperties(prefix = "crypto")
public class CryptoProperties {
    /** Source the public key (typically .pem or .der format). */
    private String publicKeySource;

    /** Source of the private key (sensitive). */
    private String privateKeySource;

    /** Hashing algorithm used for generating fixed size digest of given data */
    private String hashAlgorithm;

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public String getPublicKeySource() {
        return publicKeySource;
    }

    /**
     * @param publicKeySource The absolute or relative path to the public key file.
     */
    public void setPublicKeySource(String publicKeySource) {
        this.publicKeySource = publicKeySource;
    }

    public String getPrivateKeySource() {
        return privateKeySource;
    }

    /**
     * @param privateKeySource The absolute or relative path to the private key file.
     */
    public void setPrivateKeySource(String privateKeySource) {
        this.privateKeySource = privateKeySource;
    }
}
