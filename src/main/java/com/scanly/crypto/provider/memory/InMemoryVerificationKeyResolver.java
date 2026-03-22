package com.scanly.crypto.provider.memory;

import com.scanly.crypto.api.KeyVault;
import com.scanly.crypto.api.VerificationKeyResolver;
import com.scanly.crypto.exception.InvalidKeyOperationException;
import com.scanly.crypto.model.Jwk;
import com.scanly.crypto.model.KeyStatus;
import org.springframework.stereotype.Component;

/**
 * In-memory implementation of the {@link VerificationKeyResolver}.
 * <p>
 * This component acts as the primary interface for public cryptographic operations,
 * specifically token verification and active key selection. It enforces
 * {@link KeyStatus} rules to ensure only appropriate keys are used for
 * specific operations.
 * </p>
 *
 * <p>
 * This component acts as the primary interface for providing public details
 * for the key that can be used to verify the signature. It can be then
 * used by {@code JwtDecoder} to verify the signature.
 *
 * It is also responsible to enforce business rules to ensure only those keys
 * are made available which are currently eligible for signature verification.
 * </p>
 */
@Component
public class InMemoryVerificationKeyResolver implements VerificationKeyResolver {

    private final KeyVault keyVault;

    public InMemoryVerificationKeyResolver(KeyVault keyVault) {
        this.keyVault = keyVault;
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
    public Jwk getVerificationKey(String kid) {
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