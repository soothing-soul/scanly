package com.scanly.crypto.provider.memory;

import com.scanly.crypto.api.KeyVault;
import com.scanly.crypto.exception.KeyNotFoundException;
import com.scanly.crypto.model.Jwk;
import com.scanly.crypto.model.KeyPairContainer;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Package-private implementation of the {@link KeyVault}.
 * <p>
 * This class serves as the internal storage for cryptographic keys within
 * the memory provider.
 * </p>
 * <p>
 * <b>Note:</b> This implementation is non-persistent; all keys are lost
 * upon application shutdown.
 * </p>
 */
class InMemoryKeyVault implements KeyVault {

    /** The internal authoritative list of cryptographic key pairs. */
    private List<KeyPairContainer> keyPairContainers;

    /**
     * Updates the vault with a new set of authoritative key pairs.
     * <p>
     * This method is typically called by the {@code KeyLifecycleManager} during
     * bootstrap or after a successful key rotation.
     * </p>
     *
     * @param keyPairContainers The new list of key pairs to be held in memory.
     */
    @Override
    public void setKeyPairs(List<KeyPairContainer> keyPairContainers) {
        this.keyPairContainers = keyPairContainers;
    }

    /**
     * Projects the internal key containers into public-facing JWK representations.
     *
     * @return A list of all available keys in their public {@link Jwk} form.
     */
    @Override
    public List<Jwk> getAllJwks() {
        return keyPairContainers
                .stream()
                .map(this::transformToJwk)
                .toList();
    }

    /**
     * Locates a public key by its identifier for verification tasks.
     *
     * @param kid The unique Key ID.
     * @return The matching public {@link Jwk}.
     * @throws KeyNotFoundException if no key matches the provided ID.
     */
    @Override
    public Jwk findJwkByKid(String kid) {
        KeyPairContainer selectedKeyPairContainer = findByKid(kid);
        return transformToJwk(selectedKeyPairContainer);
    }

    /**
     * Retrieves the internal container for high-privilege operations like signing.
     *
     * @param kid The unique Key ID.
     * @return The full {@link KeyPairContainer}.
     * @throws KeyNotFoundException if no key matches the provided ID.
     */
    @Override
    public KeyPairContainer findByKid(String kid) {
        KeyPairContainer keyPairContainer = keyPairContainers
                .stream()
                .filter(keyPair -> keyPair.kid().equals(kid))
                .findFirst()
                .orElse(null);

        if (keyPairContainer == null) {
            throw new KeyNotFoundException(
                    String.format("Key not found for kid = %s", kid)
            );
        }
        return keyPairContainer;
    }

    /**
     * Maps a private {@link KeyPairContainer} to a public {@link Jwk}.
     * <p>
     * This transformation ensures that the sensitive {@link java.security.PrivateKey}
     * is stripped away before the key data leaves the vault for general usage.
     * </p>
     */
    private Jwk transformToJwk(KeyPairContainer keyPairContainer) {
        return new Jwk(
                keyPairContainer.kid(),
                keyPairContainer.alg(),
                keyPairContainer.keyStatus(),
                keyPairContainer.publicKey(),
                keyPairContainer.createdAt(),
                keyPairContainer.expiresAt()
        );
    }
}