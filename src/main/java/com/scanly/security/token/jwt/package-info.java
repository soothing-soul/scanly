/**
 * Provides a library-agnostic infrastructure for handling JSON Web Tokens (JWT)
 * within the Scanly platform.
 * * <p>This package is designed as a "Lower Level Machine" that bridges high-level
 * business features (like Authentication and Analytics) with low-level cryptographic
 * operations and third-party JOSE libraries (Nimbus).
 * * <h2>Key Components:</h2>
 * <ul>
 * <li><b>Models:</b> Immutable records like {@link com.scanly.security.token.jwt.model.Jwt}
 * and {@link com.scanly.security.token.jwt.encoding.model.JwtPayload} serve as the
 * universal currency for token data across the application.</li>
 * <li><b>Encoding:</b> The {@code encoding} sub-package handles the creation of
 * standardized, ES256-signed tokens using custom Base62 unique identifiers.</li>
 * <li><b>Decoding:</b> The {@code decoding} sub-package provides dynamic key
 * resolution via {@code kid} (Key ID) headers, allowing for seamless key rotation
 * and multi-purpose token verification.</li>
 * <li><b>Exceptions:</b> A hierarchical error system that translates technical
 * cryptographic failures into platform-consistent {@code ScanlyBaseException} types.</li>
 * </ul>
 * * <h2>Architecture Principle:</h2>
 * This package adheres to the principle of <b>Infrastructure Decoupling</b>. While it
 * utilizes Spring Security and Nimbus internally, it exposes only Scanly-specific
 * interfaces and domain models to the rest of the application. This ensures that
 * the "Business Layer" remains protected from changes in underlying security providers.
 * * @since 1.0
 * @author Vijay
 */
package com.scanly.security.token.jwt;