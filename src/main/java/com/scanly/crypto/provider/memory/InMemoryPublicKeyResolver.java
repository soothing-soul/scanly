package com.scanly.crypto.provider.memory;

import com.scanly.crypto.api.KeyVault;
import com.scanly.crypto.api.PublicKeyResolver;
import com.scanly.crypto.exception.InvalidKeyOperationException;
import com.scanly.crypto.exception.NoActiveKeyAvailableException;
import com.scanly.crypto.model.Jwk;
import com.scanly.crypto.model.KeyStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * In-memory implementation of the {@link PublicKeyResolver}.
 * <p>
 * This component acts as the primary interface for public cryptographic operations,
 * specifically token verification and active key selection. It enforces
 * {@link KeyStatus} rules to ensure only appropriate keys are used for
 * specific operations.
 * </p>
 */
@Component
public class InMemoryPublicKeyResolver implements PublicKeyResolver {

    private final KeyVault keyVault;

    public InMemoryPublicKeyResolver(KeyVault keyVault) {
        this.keyVault = keyVault;
    }

    /**
     * Selects an eligible key for a new signature operation.
     * <p>
     * Filters all available keys in the vault for those with an {@code ACTIVE} status.
     * If multiple keys are active, it selects one at random to distribute
     * cryptographic load.
     * </p>
     *
     * @return A random {@link Jwk} eligible for signing.
     * @throws NoActiveKeyAvailableException if no key in the vault is currently {@code ACTIVE}.
     */
    @Override
    public Jwk getActiveJwk() {
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

    /**
     * Resolves a public key by its ID and validates its eligibility for verification.
     * <p>
     * Even if a key exists in the vault, this method ensures the key's
     * {@link KeyStatus} explicitly allows verification (e.g., it hasn't
     * been revoked or fully decommissioned).
     * </p>
     *
     * @param kid The unique Key ID.
     * @return The validated {@link Jwk}.
     * @throws InvalidKeyOperationException if the key exists but its status prohibits verification.
     */
    @Override
    public Jwk getJwk(String kid) {
        Jwk jwk = keyVault.findJwkByKid(kid);

        // Enforce the lifecycle check
        if (jwk.keyStatus().canVerify()) {
            return jwk;
        }

        throw new InvalidKeyOperationException(
                String.format("Key with kid=%s is not available for verification", kid)
        ).withContext("status", jwk.keyStatus().name());
    }
}