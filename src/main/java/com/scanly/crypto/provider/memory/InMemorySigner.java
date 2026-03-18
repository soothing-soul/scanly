package com.scanly.crypto.provider.memory;

import com.scanly.crypto.api.PrivateKeyResolver;
import com.scanly.crypto.api.Signer;
import com.scanly.crypto.exception.AlgorithmNotFoundException;
import com.scanly.crypto.exception.InternalCryptoException;
import com.scanly.crypto.model.KeyPairContainer;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

/**
 * The core signing engine for the Scanly platform.
 * <p>
 * This class implements the digital signature logic (RSA/ECDSA) using
 * keys retrieved via the {@link PrivateKeyResolver}.
 * </p>
 * <p>
 * Because this class is package-private, it cannot be manually instantiated
 * by domain services. It must be obtained via the
 * {@link CryptoMemoryProviderConfig} as a Spring-managed bean.
 * </p>
 */
public class InMemorySigner implements Signer {
    private final PrivateKeyResolver keyResolver;

    public InMemorySigner(PrivateKeyResolver keyResolver) {
        this.keyResolver = keyResolver;
    }

    /**
     * Generates a digital signature for the provided input using JCA providers.
     * <p>
     * The process involves three steps:
     * <ol>
     * <li>Resolving the private key and verifying its signing status.</li>
     * <li>Initializing the Signature engine with the algorithm defined by the key (e.g., ES256).</li>
     * <li>Executing the cryptographic math to produce the signature bytes.</li>
     * </ol>
     * </p>
     *
     * @param kid          The unique identifier of the key pair to use.
     * @param signingInput The raw data payload to be signed.
     * @return The raw byte array of the digital signature.
     * @throws AlgorithmNotFoundException if the specified algorithm is not supported by the JVM.
     * @throws InternalCryptoException if the key is invalid for signing or the process fails.
     */
    @Override
    public byte[] sign(String kid, byte[] signingInput) {
        // 1. Authorized access to the Private Key
        KeyPairContainer keyPairContainer = keyResolver.getKeyPairContainer(kid);

        try {
            // 2. Setup the JCA Signature engine
            Signature signature = Signature.getInstance(keyPairContainer.alg().getSignatureAlgorithm());
            signature.initSign(keyPairContainer.privateKey());
            signature.update(signingInput);

            // 3. Perform the signing
            return signature.sign();

        } catch (NoSuchAlgorithmException e) {
            // Re-map to platform exception for environment issues
            throw new AlgorithmNotFoundException(
                    String.format("Algorithm %s not found", keyPairContainer.alg().getSignatureAlgorithm()), e
            );
        } catch (InvalidKeyException | SignatureException e) {
            // Re-map to platform exception for runtime failures
            throw new InternalCryptoException(
                    String.format(
                            "Failed to sign with kid=%s using algorithm %s",
                            kid,
                            keyPairContainer.alg().getSignatureAlgorithm()
                    ),
                    e
            );
        }
    }
}