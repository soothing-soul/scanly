package com.scanly.security.token.jwt.decoding.provider.nimbus;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.scanly.crypto.api.PublicKeyResolver;
import com.scanly.crypto.exception.KeyNotFoundException;
import com.scanly.crypto.model.Jwk;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;

/**
 * Custom implementation of {@link JWSKeySelector} that resolves public keys
 * dynamically based on the Key ID (kid) present in the JWT header.
 * <p>
 * This component acts as the "search engine" for the decoding process. It ensures
 * that the system can verify signatures from multiple keys (e.g., during a
 * key rotation period) by fetching the specific public key associated with
 * a token's identity.
 * </p>
 */
@Component
class PublicKeySelector implements JWSKeySelector<SecurityContext> {

    /** The internal crypto service responsible for locating keys */
    private final PublicKeyResolver publicKeyResolver;

    public PublicKeySelector(PublicKeyResolver publicKeyResolver) {
        this.publicKeyResolver = publicKeyResolver;
    }

    /**
     * Identifies the correct public key to verify an incoming JWS signature.
     * <p>
     * It extracts the 'kid' parameter from the {@link JWSHeader} and queries
     * the {@link PublicKeyResolver} to retrieve the corresponding public key.
     * </p>
     *
     * @param header  The JWS header containing the algorithm and Key ID.
     * @param context Optional security context (not utilized in this implementation).
     * @return A list containing the single matching {@link Key} candidate.
     */
    @Override
    public List<? extends Key> selectJWSKeys(JWSHeader header, SecurityContext context) {
        // 1. Extract the Key ID from the token header
        String kid = header.getKeyID();

        // 2. Resolve the matching Jwk (containing the PublicKey) from the crypto store
        Jwk jwk = publicKeyResolver.getJwk(kid);

        // 3. Return the key as a candidate for the Nimbus verification engine
        return List.of(jwk.publicKey());
    }
}