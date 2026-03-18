package com.scanly.crypto.provider.memory;

import com.scanly.crypto.api.KeyVault;
import com.scanly.crypto.api.PrivateKeyResolver;
import com.scanly.crypto.api.Signer;
import com.scanly.crypto.exception.InvalidKeyOperationException;
import com.scanly.crypto.exception.KeyNotFoundException;
import com.scanly.crypto.model.KeyPairContainer;
import com.scanly.crypto.model.KeyStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * In-memory implementation of the {@link PrivateKeyResolver}.
 * <p>
 * This component provides secure access to full cryptographic key pairs
 * (including private keys). It acts as a gatekeeper for signing operations,
 * ensuring that only keys with an appropriate {@link KeyStatus}
 * (ACTIVE or RETIRING) are released to the signing engine.
 * </p>
 *
 * Access is restricted to the package level to ensure that key resolution
 * logic remains encapsulated within the crypto provider.
 */
public class InMemoryPrivateKeyResolver implements PrivateKeyResolver {
    private final KeyVault keyVault;

    public InMemoryPrivateKeyResolver(KeyVault keyVault) {
        this.keyVault = keyVault;
    }

    /**
     * Resolves a full key pair and validates its eligibility for digital signing.
     * <p>
     * This method is the primary gateway for the {@link Signer}.
     * It ensures that even if a key exists in storage, it cannot be used to
     * generate new signatures if its lifecycle status prohibits it.
     * </p>
     *
     * @param kid The unique Key ID.
     * @return The validated {@link KeyPairContainer} containing the private key.
     * @throws InvalidKeyOperationException if the key status does not allow signing.
     * @throws KeyNotFoundException if the kid is unrecognized.
     */
    @Override
    public KeyPairContainer getKeyPairContainer(String kid) {
        // 1. Fetch from the underlying storage
        KeyPairContainer keyPairContainer = keyVault.findByKid(kid);

        // 2. Enforce signing-specific lifecycle logic
        if (keyPairContainer.keyStatus().canSign()) {
            return keyPairContainer;
        }

        // 3. Fail loudly if the key is in a read-only or expired state
        throw new InvalidKeyOperationException(
                String.format("Key with kid=%s is not valid for signature", kid)
        ).withContext("status", keyPairContainer.keyStatus().name());
    }
}