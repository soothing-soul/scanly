package com.scanly.crypto.provider.memory;

import com.scanly.crypto.api.KeyVault;
import com.scanly.crypto.api.PrivateKeyResolver;
import com.scanly.crypto.api.Signer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the Memory-based Cryptographic Provider.
 * <p>
 * This class serves as the centralized registrar for all cryptographic beans
 * within the {@code com.scanly.crypto.provider.memory} package. By defining
 * these beans explicitly here, we ensure a strict dependency hierarchy and
 * maintain the encapsulation of package-private implementation classes.
 * </p>
 */
@Configuration
public class CryptoMemoryProviderConfig {

    /**
     * Instantiates the core KeyVault for the application.
     * <p>
     * The {@link KeyVault} is the foundational data store for raw cryptographic keys.
     * In this memory-based implementation, keys are held in volatile memory and
     * are not persisted across application restarts.
     * </p>
     * * @return in-memory implementation of the {@link KeyVault}.
     */
    @Bean
    public KeyVault keyVault() {
        // Implementation is package-private to prevent external instantiation.
        return new InMemoryKeyVault();
    }

    /**
     * Instantiates the PrivateKeyResolver with its required KeyVault dependency.
     * <p>
     * This service sits between the high-level Signer and the low-level KeyVault,
     * providing the logic necessary to resolve specific keys via their Key IDs (KIDs).
     * </p>
     * * @param keyVault the primary source of cryptographic keys.
     * @return an implementation of {@link PrivateKeyResolver} capable of lookup operations.
     */
    @Bean
    public PrivateKeyResolver privateKeyResolver(KeyVault keyVault) {
        // We inject the interface KeyVault to maintain decoupled logic.
        return new InMemoryPrivateKeyResolver(keyVault);
    }

    /**
     * Instantiates the high-level Signer used by domain services.
     * <p>
     * The {@link Signer} is the primary interface used by the rest of the application
     * (e.g., JWT generation, PDF signing) to perform digital signatures. It relies
     * on the {@link PrivateKeyResolver} to handle key retrieval internally.
     * </p>
     * * @param privateKeyResolver the resolver used to fetch signing keys.
     * @return the primary signing service for the Scanly platform.
     */
    @Bean
    public Signer signer(PrivateKeyResolver privateKeyResolver) {
        /* * Note: We inject the concrete InMemoryPrivateKeyResolver here to
         * strictly bind the Signer to this memory provider's resolver implementation.
         */
        return new InMemorySigner(privateKeyResolver);
    }
}