package com.scanly.crypto.api;

import com.scanly.crypto.exception.KeyLoadingException;
import com.scanly.crypto.model.Algorithm;
import com.scanly.crypto.model.KeyPairContainer;

/**
 * Handles the extraction and instantiation of cryptographic keys from external sources.
 * <p>
 * The {@code KeyLoader} is responsible for the low-level I/O operations required to
 * fetch raw key material (e.g., PEM or DER files) and transform them into
 * platform-standard {@link KeyPairContainer} objects.
 * </p>
 * <p>
 * Implementations typically handle:
 * <ul>
 * <li>File system I/O or network requests to fetch key bytes.</li>
 * <li>Parsing PEM headers and footers.</li>
 * <li>Decoding Base64-encoded key material.</li>
 * <li>Utilizing {@link java.security.KeyFactory} to generate the final key objects.</li>
 * </ul>
 * </p>
 */
public interface KeyLoader {

    /**
     * Loads an asymmetric key pair from the specified source paths.
     * <p>
     * This method performs the heavy lifting of reading the physical bytes,
     * identifying the key family based on the provided {@link Algorithm},
     * and assembling a complete {@link KeyPairContainer}.
     * </p>
     *
     * @param publicKeyPath  The location (file path or URI) of the public key material.
     * @param privateKeyPath The location (file path or URI) of the private key material.
     * @param algorithm      The cryptographic algorithm these keys are intended for.
     * @return A fully populated {@link KeyPairContainer} ready for signing and verification.
     * @throws KeyLoadingException if the files are missing, corrupted, or the format is invalid.
     */
    KeyPairContainer loadKeyPair(String publicKeyPath, String privateKeyPath, Algorithm algorithm);
}