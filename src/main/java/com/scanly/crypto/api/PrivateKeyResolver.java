package com.scanly.crypto.api;

import com.scanly.crypto.exception.KeyNotFoundException;
import com.scanly.crypto.model.KeyPairContainer;

/**
 * High-level API for resolving full key pairs for signing operations.
 * <p>
 * This interface is a high-privilege component of the Scanly crypto package.
 * It is responsible for retrieving the {@link KeyPairContainer}, which includes
 * the sensitive {@link java.security.PrivateKey} required to generate
 * digital signatures.
 * </p>
 * <p>
 * <b>Security Warning:</b> Access to implementations of this interface should
 * be strictly restricted to internal signing services and core security
 * infrastructure.
 * </p>
 */
public interface PrivateKeyResolver {

    /**
     * Retrieves the full key pair container associated with a specific Key ID.
     * <p>
     * This method is the gateway to the platform's signing capability. It allows
     * the {@link Signer} to access the private material necessary to secure JWTs
     * or other signed payloads.
     * </p>
     *
     * @param kid The unique Key ID assigned to the key pair.
     * @return The {@link KeyPairContainer} containing both the public and private keys.
     * @throws KeyNotFoundException if no key pair exists with the requested identifier.
     */
    KeyPairContainer getKeyPairContainer(String kid);
}