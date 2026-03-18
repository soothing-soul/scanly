package com.scanly.crypto.api;

import com.scanly.crypto.exception.CryptoException;

/**
 * Defines the contract for generating digital signatures within the Scanly platform.
 * <p>
 * This interface abstracts the cryptographic signing process, allowing callers
 * to generate secure signatures without direct exposure to private keys or
 * underlying cryptographic providers (JCA/BouncyCastle).
 * </p>
 * <p>
 * Implementations are responsible for:
 * <ul>
 * <li>Resolving the private key associated with the provided Key ID (kid).</li>
 * <li>Configuring the signature engine with the correct algorithm (e.g., ES256).</li>
 * <li>Executing the mathematical signing operation on the input data.</li>
 * </ul>
 * </p>
 */
public interface Signer {
    /**
     * Generates a digital signature for the provided input using the specified key.
     * <p>
     * The resulting byte array is the raw cryptographic signature, which may
     * require transcoding (e.g., from DER to R+S concatenated) depending on
     * the requirements of the higher-level protocol like JWS.
     * </p>
     *
     * @param kid          The unique identifier of the key pair to be used for signing.
     * @param signingInput The raw byte payload to be cryptographically signed.
     * @return The resulting digital signature as a byte array.
     * @throws CryptoException if the signing fails due to provider issues or key mismatches.
     */
    byte[] sign(String kid, byte[] signingInput);
}