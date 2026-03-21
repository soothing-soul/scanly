package com.scanly.security.token.jwt.encoding.provider.nimbus;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.impl.ECDSA;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.util.Base64URL;
import com.scanly.crypto.api.Signer;
import com.scanly.security.token.jwt.config.JwtProperties;
import com.scanly.security.token.jwt.exception.AlgorithmNotSupportedException;
import com.scanly.security.token.jwt.exception.EcdsaSignatureTranscodingException;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * A custom adapter that bridges the Nimbus JOSE library with Scanly's internal
 * cryptographic {@link Signer}.
 * <p>
 * This class implements the {@link JWSSigner} interface, using the Nimbus high-level
 * JWT API while delegating the sensitive operation of digital signing to a specialized,
 * domain-agnostic crypto package.
 * </p>
 * <p>
 * <b>Security Note:</b> This design ensures that private keys never leave the
 * {@code crypto} package, as the {@code NimbusSignerAdapter} only passes
 * raw bytes to the internal signer and receives the signature in return.
 * </p>
 */
@Component
class NimbusSignerAdapter implements JWSSigner {

    /** The set of JWS algorithms officially supported by the Scanly platform */
    private final Set<JWSAlgorithm> supportedAlgorithms;

    /** The internal Scanly cryptographic engine that performs the actual signing operations. */
    private final Signer signer;

    /**
     * Constructs the adapter, initializing the list of allowed signing algorithms
     * from the application's global JWT properties.
     * @param jwtProperties The source of truth for platform-wide JWT configurations.
     * @param signer The internal engine responsible for private key management.
     */
    public NimbusSignerAdapter(JwtProperties jwtProperties, Signer signer) {
        this.signer = signer;
        this.supportedAlgorithms = Set.copyOf(
                jwtProperties
                        .getSupportedAlgorithms()
                        .stream()
                        .map(JWSAlgorithm::parse)
                        .toList()
        );
    }

    /**
     * Signs the provided JWS input using the internal cryptographic machine.
     * <p>
     * The process involves:
     * <ol>
     * <li>Validating that the requested algorithm matches platform policy.</li>
     * <li>Extracting the Key ID (kid) from the header to locate the correct private key.</li>
     * <li>Delegating the raw signature generation to the {@code signer}.</li>
     * <li>Transcoding the signature to the JWS-compliant format if necessary (e.g., ECDSA).</li>
     * </ol>
     * </p>
     *
     * @param header       The JWS header containing metadata such as the Key ID and Algorithm.
     * @param signingInput The combined, Base64URL-encoded bytes of the Header and Payload.
     * @return A {@link Base64URL} representing the finalized digital signature.
     * @throws AlgorithmNotSupportedException If the header specifies an algorithm not in the approved set.
     */
    @Override
    public Base64URL sign(JWSHeader header, byte[] signingInput) {
        String kid = header.getKeyID();
        JWSAlgorithm alg = header.getAlgorithm();

        if (!supports(alg)) {
            throw new AlgorithmNotSupportedException(
                    String.format("Algorithm '%s' is not supported by the current Scanly policy.", alg)
            );
        }

        // Request a raw signature from the internal crypto package
        byte[] signature = signer.sign(kid, signingInput);

        // Perform necessary format transformations (e.g., DER to R+S for ECDSA)
        signature = transformToJoseFormat(signature, alg);

        return Base64URL.encode(signature);
    }

    /**
     * Transcodes a raw signature into the format required by the JOSE specification.
     * <p>
     * Standard Java {@code Signature} implementations produce ECDSA signatures in
     * ASN.1 DER format. However, JWS requires ECDSA signatures (like ES256) to be
     * provided as a concatenated bitstream of the R and S values (64 bytes for P-256).
     * </p>
     * * @param signature The raw signature bytes produced by the Java Security provider.
     * @param alg       The JWS algorithm being used.
     * @return The transcoded signature ready for JWT embedding.
     * @throws EcdsaSignatureTranscodingException If the DER-to-Concat conversion fails.
     */
    private byte[] transformToJoseFormat(byte[] signature, JWSAlgorithm alg) {
        if (!isTranscodingRequired(alg)) {
            return signature;
        }
        try {
            // For ES256 (P-256), the result must be exactly 64 bytes
            return ECDSA.transcodeSignatureToConcat(signature, 64);
        } catch (JOSEException e) {
            throw new EcdsaSignatureTranscodingException(
                    "Critical failure during ECDSA DER-to-R+S transcoding for algorithm: " + alg.getName(), e
            );
        }
    }

    /**
     * ES256 is currently the only algorithm requiring R+S concatenation in our setup
     * @param alg
     * @return Whether the signature from given algorithm requires transcoding
     */
    private boolean isTranscodingRequired(JWSAlgorithm alg) {
        return JWSAlgorithm.ES256.equals(alg);
    }

    /**
     * Checks if the requested algorithm is permitted by platform configuration.
     */
    private boolean supports(JWSAlgorithm algorithm) {
        return supportedAlgorithms.contains(algorithm);
    }

    /**
     * @return An unmodifiable set of algorithms this signer is permitted to handle.
     */
    @Override
    public Set<JWSAlgorithm> supportedJWSAlgorithms() {
        return Set.copyOf(supportedAlgorithms);
    }

    /**
     * Returns a standard JCA context for the Nimbus framework.
     */
    @Override
    public JCAContext getJCAContext() {
        return new JCAContext();
    }
}