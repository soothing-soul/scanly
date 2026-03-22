package com.scanly.crypto.provider.memory;

import com.scanly.crypto.api.KeyVault;
import com.scanly.crypto.api.SigningKeyResolver;
import com.scanly.crypto.exception.NoActiveKeyAvailableException;
import com.scanly.crypto.model.Jwk;
import com.scanly.crypto.model.KeyStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * In-memory implementation of the {@link SigningKeyResolver}.
 * <p>
 * This component acts as the primary interface for providing metadata
 * for the key that can be used to perform the signature. It can be then
 * used by {@code JwtEncoder} to generate the JwtHeader with (kid, alg)
 * etc.
 * It enforces business rules to ensure only those keys are made available
 * which are currently eligible for generating signature.
 * </p>
 */
@Component
public class InMemorySigningKeyResolver implements SigningKeyResolver {

    private final KeyVault keyVault;

    public InMemorySigningKeyResolver(KeyVault keyVault) {
        this.keyVault = keyVault;
    }

    /**
     * Selects an eligible key for a new signature operation.
     * <p>
     * Ensures only those keys are made available which are eligible for performing
     * signature.  If multiple keys are available, it will select any random key.
     * </p>
     *
     * @return A random {@link Jwk} eligible for signing.
     * @throws NoActiveKeyAvailableException if no key in the vault is currently {@code ACTIVE}.
     */
    @Override
    public Jwk getSigningKeyMetadata() {
        List<Jwk> activeJwkList = keyVault.getAllJwks()
                .stream()
                .filter(jwk -> jwk.keyStatus().isActive())
                .toList();

        if (activeJwkList.isEmpty()) {
            throw new NoActiveKeyAvailableException("No active key available for signature");
        }

        // Load-balancing logic for multi-key environments
        return activeJwkList.get(ThreadLocalRandom.current().nextInt(activeJwkList.size()));
    }
}