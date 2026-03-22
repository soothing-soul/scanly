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
 *
 * <p>
 * This interface has intentionally been made generic to ensure support for different
 * type of {@code KeySource} to fetch the actual keys from. It could be a String that
 * contains the path on the local filesystem, or it could be a complex object containing
 * the detail about a remote server and the path of the keys there.
 * </p>
 */
public interface KeyLoader<T> {

    /**
     * Loads an asymmetric key pair from the specified source paths.
     * <p>
     * This method performs the heavy lifting of reading the physical bytes,
     * identifying the key family based on the provided {@link Algorithm},
     * and assembling a complete {@link KeyPairContainer}.
     * </p>
     *
     * <p>
     * <b>Security Note:</b> Implementation should be aware that {@code privateKeySource}
     * is allowed to be null if the security policy requires private key to not be exposed
     * directly.
     * </p>
     *
     * @param publicKeySource  The location (file path or URI) of the public key material.
     * @param privateKeySource The location (file path or URI) of the private key material.
     * @param algorithm      The cryptographic algorithm these keys are intended for.
     * @return A fully populated {@link KeyPairContainer} ready for signing and verification.
     * @throws KeyLoadingException if the files are missing, corrupted, or the format is invalid.
     */
    KeyPairContainer loadKeyPair(T publicKeySource, T privateKeySource, Algorithm algorithm);
}