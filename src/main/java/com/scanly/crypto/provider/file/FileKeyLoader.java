package com.scanly.crypto.provider.file;

import com.scanly.crypto.api.HashGenerator;
import com.scanly.crypto.api.KeyLoader;
import com.scanly.crypto.exception.AlgorithmNotFoundException;
import com.scanly.crypto.exception.KeyEncodingException;
import com.scanly.crypto.exception.KeyLoadingException;
import com.scanly.crypto.model.Algorithm;
import com.scanly.crypto.model.KeyPairContainer;
import com.scanly.crypto.model.KeyStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

/**
 * A filesystem-based implementation of the {@link KeyLoader} interface.
 * <p>
 * This component is responsible for reading PEM-encoded key files from the disk,
 * stripping metadata headers, and instantiating cryptographic key objects using
 * the Java Cryptography Architecture (JCA).
 * </p>
 */
@ConditionalOnProperty(
        prefix = "crypto",
        name = "key-source-type",
        havingValue = "file"
)
@Component
public class FileKeyLoader implements KeyLoader<String> {
    private final HashGenerator hashGenerator;

    public FileKeyLoader(HashGenerator hashGenerator) {
        this.hashGenerator = hashGenerator;
    }

    /**
     * Orchestrates the loading of a complete asymmetric key pair.
     * <p>
     * It extracts both public and private components, generates a unique
     * Key ID based on the public key's fingerprint, and assigns a default
     * longevity to the key pair.
     * </p>
     *
     * @param publicKeySource  The filesystem path to the public key (.pem).
     * @param privateKeySource The filesystem path to the private key (.pem).
     * @param algorithm      The {@link Algorithm} context for the key factory.
     * @return A fully populated {@link KeyPairContainer}.
     */
    @Override
    public KeyPairContainer loadKeyPair(String publicKeySource, String privateKeySource, Algorithm algorithm) {
        PublicKey publicKey = loadPublicKey(publicKeySource, algorithm);
        PrivateKey privateKey = isInvalidPath(privateKeySource)
                ? null
                : loadPrivateKey(privateKeySource, algorithm);

        // Deterministic ID generation based on public key content
        String kid = generateKeyId(publicKey);

        return new KeyPairContainer(
                kid,
                algorithm,
                KeyStatus.ACTIVE,
                publicKey,
                privateKey,
                Instant.now(),
                Instant.now().plusSeconds(1_000_000_000L) // Long-term expiration for local dev
        );
    }

    private boolean isInvalidPath(String privateKeySource) {
        return privateKeySource == null || privateKeySource.isEmpty();
    }

    /**
     * Reads and parses a PKCS#8 encoded private key.
     */
    private PrivateKey loadPrivateKey(String privateKeyPath, Algorithm algorithm) {
        try {
            String content = FileReader.readFileAsString(privateKeyPath);
            byte[] decoded = decodeKeyContent(
                    content,
                    "-----BEGIN PRIVATE KEY-----",
                    "-----END PRIVATE KEY-----"
            );
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm.getKeyFamily());
            return keyFactory.generatePrivate(keySpec);
        } catch (IOException e) {
            throw new KeyLoadingException("Unable to read private key file: " + privateKeyPath, e);
        } catch (NoSuchAlgorithmException e) {
            throw new AlgorithmNotFoundException("JCA Provider not found for: " + algorithm.getKeyFamily(), e);
        } catch (InvalidKeySpecException e) {
            throw new KeyEncodingException("Private key is not in a valid PKCS#8 format: " + privateKeyPath, e);
        }
    }

    /**
     * Reads and parses an X.509 encoded public key.
     */
    private PublicKey loadPublicKey(String publicKeyPath, Algorithm algorithm) {
        try {
            String content = FileReader.readFileAsString(publicKeyPath);
            byte[] decoded = decodeKeyContent(
                    content,
                    "-----BEGIN PUBLIC KEY-----",
                    "-----END PUBLIC KEY-----"
            );
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm.getKeyFamily());
            return keyFactory.generatePublic(keySpec);
        } catch (IOException e) {
            throw new KeyLoadingException("Unable to read public key file: " + publicKeyPath, e);
        } catch (NoSuchAlgorithmException e) {
            throw new AlgorithmNotFoundException("JCA Provider not found for: " + algorithm.getKeyFamily(), e);
        } catch (InvalidKeySpecException e) {
            throw new KeyEncodingException("Public key is not in a valid X.509 format: " + publicKeyPath, e);
        }
    }

    /**
     * Sanitizes PEM content by removing headers, footers, and whitespace
     * before decoding from Base64.
     */
    private byte[] decodeKeyContent(String pem, String prefix, String suffix) {
        pem = pem.replace(prefix, "")
                .replace(suffix, "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(pem);
    }

    /**
     * Generates a unique, URL-safe Key ID by hashing the public key's encoded bytes.
     * <p>
     * This ensures that the same physical key always results in the same 'kid'
     * across different application instances.
     * </p>
     */
    private String generateKeyId(PublicKey publicKey) {
        byte[] result = hashGenerator.generateHash(publicKey.getEncoded());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(result);
    }
}
