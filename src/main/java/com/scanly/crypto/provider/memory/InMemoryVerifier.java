package com.scanly.crypto.provider.memory;

import com.scanly.crypto.api.PublicKeyResolver;
import com.scanly.crypto.api.Verifier;
import com.scanly.crypto.exception.KeyNotFoundException;
import com.scanly.crypto.exception.VerificationFailedException;
import com.scanly.crypto.model.Jwk;

import java.security.*;

/**
 * Memory-based implementation of the {@link Verifier} interface.
 * <p>
 * This class leverages the Java Standard Edition Security API to validate
 * signatures. It relies on {@link Jwk} metadata to dynamically support
 * different signature algorithms (e.g., RS256, ES256).
 * </p>
 */
public class InMemoryVerifier implements Verifier {
    private final PublicKeyResolver publicKeyResolver;

    /**
     * Constructs a new InMemoryVerifier.
     * @param publicKeyResolver The resolver used to fetch public keys and algorithm metadata.
     */
    public InMemoryVerifier(PublicKeyResolver publicKeyResolver) {
        this.publicKeyResolver = publicKeyResolver;
    }

    /**
     * Verifies a digital signature against the provided data.
     * <p>
     * This method returns {@code false} only if the cryptographic check fails
     * (indicating tampered data or an incorrect key). For all other scenarios
     * involving missing keys, unsupported algorithms, or internal failures,
     * a {@link VerificationFailedException} is thrown to signal a system-level issue.
     * </p>
     *
     * @param kid       The Key ID used to identify the public key.
     * @param data      The original raw data.
     * @param signature The signature to be validated.
     * @return {@code true} if valid, {@code false} if the signature is mathematically incorrect.
     * @throws VerificationFailedException if a system error prevents the check from completing.
     */
    @Override
    public boolean verify(String kid, byte[] data, byte[] signature) {
        try {
            // 1. Resolve JWK metadata (Key + Algorithm)
            Jwk jwk = publicKeyResolver.getJwk(kid);
            PublicKey publicKey = jwk.publicKey();
            String signatureAlgorithm = jwk.alg().getSignatureAlgorithm();

            // 2. Initialize the Cryptographic Engine
            Signature sig = Signature.getInstance(signatureAlgorithm);
            sig.initVerify(publicKey);
            sig.update(data);

            // 3. Mathematical Verification (The only path to 'false')
            return sig.verify(signature);

        } catch (KeyNotFoundException e) {
            throw new VerificationFailedException(
                    String.format("Key with kid=%s is either not available or not authorized to verify anymore", kid),
                    e
            );
        } catch (NoSuchAlgorithmException e) {
            throw new VerificationFailedException(
                    String.format("Algorithm of PublicKey with kid=%s is not supported.", kid),
                    e
            );
        } catch (InvalidKeyException e) {
            throw new VerificationFailedException(
                    String.format("PublicKey with kid=%s is not valid", kid),
                    e
            );
        } catch (SignatureException e) {
            throw new VerificationFailedException(
                    String.format(
                            "Internal cryptographic failure occurred during verification by PublicKey with kid=%s",
                            kid
                    ),
                    e
            );
        }
    }
}