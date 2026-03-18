/**
 * Provides the cryptographic engine for the Scanly platform, handling the lifecycle,
 * storage, and application of asymmetric key material.
 * <p>
 * This package follows a strictly decoupled architecture:
 * <ul>
 * <li><b>Ingestion ({@link com.scanly.crypto.api.KeyLoader}):</b> Abstracts where keys come
 * from, allowing seamless switching between local filesystem and remote cloud storage.</li>
 * <li><b>Storage ({@link com.scanly.crypto.api.KeyVault}):</b> A "dumb" repository for persisting
 * and retrieving key containers without enforcing business logic.</li>
 * <li><b>Access ({@link com.scanly.crypto.api.PublicKeyResolver}, {@link com.scanly.crypto.api.PrivateKeyResolver}):</b>
 * Implements the Interface Segregation Principle to provide "least-privilege" access to keys.</li>
 * <li><b>Execution ({@link com.scanly.crypto.api.Signer}):</b> The high-level action interface
 * that performs digital signatures (e.g., ES256) while hiding JCA complexity.</li>
 * </ul>
 * </p>
 * <p>
 * <b>Security Principle:</b> Private keys are encapsulated within the {@code KeyPairContainer}
 * and are only exposed through high-privilege resolvers, ensuring that business-level
 * logic (like JWT decoding) only ever interacts with public material ({@link com.scanly.crypto.model.Jwk}).
 * </p>
 *
 * @since 1.0
 * @author Vijay
 */
package com.scanly.crypto;