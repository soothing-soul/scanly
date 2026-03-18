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
    /** The filesystem path to the public key (typically .pem or .der format). */
    private String publicKeyPath;

    /** The filesystem path to the private key (sensitive). */
    private String privateKeyPath;

    /** Hashing algorithm used for generating fixed size digest of given data */
    private String hashAlgorithm;

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    /**
     * @param publicKeyPath The absolute or relative path to the public key file.
     */
    public void setPublicKeyPath(String publicKeyPath) {
        this.publicKeyPath = publicKeyPath;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    /**
     * @param privateKeyPath The absolute or relative path to the private key file.
     */
    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }
}
