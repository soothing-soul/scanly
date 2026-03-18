package com.scanly.crypto.manager;

import com.scanly.crypto.api.KeyLoader;
import com.scanly.crypto.api.KeyVault;
import com.scanly.crypto.config.CryptoProperties;
import com.scanly.crypto.model.Algorithm;
import com.scanly.crypto.model.KeyPairContainer;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Orchestrates the end-to-end lifecycle of cryptographic keys within the platform.
 * <p>
 * The {@code KeyLifecycleManager} is the central authority for key bootstrapping
 * and rotation. It coordinates between the {@link KeyLoader} (to fetch material
 * from external sources) and the {@link KeyVault} (to provide keys to the
 * application's internal services).
 * </p>
 */
@Component
public class KeyLifecycleManager {
    private final KeyLoader keyLoader;
    private final KeyVault keyVault;
    private final CryptoProperties cryptoProperties;

    /**
     * Initializes the manager with its required strategy for loading and storing keys.
     *
     * @param keyLoader The strategy used to parse key files from the filesystem.
     * @param keyVault  The repository used to hold keys in memory for the application.
     */
    public KeyLifecycleManager(
            KeyLoader keyLoader,
            KeyVault keyVault,
            CryptoProperties cryptoProperties) {
        this.keyLoader = keyLoader;
        this.keyVault = keyVault;
        this.cryptoProperties = cryptoProperties;
    }

    /**
     * Bootstraps the cryptographic ecosystem during application startup.
     * <p>
     * This method is automatically invoked by Spring after dependency injection.
     * It triggers the initial load of the platform's ES256 key pair and populates
     * the {@link KeyVault}, effectively "priming" the platform for token
     * signing and verification.
     * </p>
     */
    @PostConstruct
    public void initializeKeys() {
        // 1. Ingest the key pair from the specified file paths
        KeyPairContainer keyPairContainer = keyLoader.loadKeyPair(
                cryptoProperties.getPublicKeyPath(),
                cryptoProperties.getPrivateKeyPath(),
                Algorithm.ES256
        );

        // 2. Synchronize the Vault with the newly loaded authoritative key set
        keyVault.setKeyPairs(List.of(keyPairContainer));
    }
}