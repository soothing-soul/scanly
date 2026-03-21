package com.scanly.security.token.jwt.constants;

/**
 * Constants representing standard Registered Claim Names as defined in RFC 7519 Section 4.1.
 * These constants ensure consistency across the Scanly platform when encoding and
 * decoding JWT payloads, preventing errors caused by misspelled claim keys.
 */
public final class JwtConstants {

    /**
     * Prevents instantiation of this utility constant class.
     */
    private JwtConstants() {}

    /**
     * "iss" (Issuer) Claim: Identifies the principal that issued the JWT.
     */
    public static final String ISSUER = "iss";

    /**
     * "iat" (Issued At) Claim: Identifies the time at which the JWT was issued.
     * Used to determine the age of the token.
     */
    public static final String ISSUED_AT = "iat";

    /**
     * "exp" (Expiration Time) Claim: Identifies the expiration time on or after which
     * the JWT MUST NOT be accepted for processing.
     */
    public static final String EXPIRES_AT = "exp";

    /**
     * "nbf" (Not Before) Claim: Identifies the time before which the JWT MUST NOT
     * be accepted for processing.
     */
    public static final String NOT_BEFORE = "nbf";

    /**
     * "sub" (Subject) Claim: Identifies the principal that is the subject of the JWT.
     * In the Scanly platform, this is typically the user's email or unique identifier.
     */
    public static final String SUBJECT = "sub";

    /**
     * "aud" (Audience) Claim: Identifies the recipients that the JWT is intended for.
     */
    public static final String AUDIENCE = "aud";

    /**
     * "jti" (JWT ID) Claim: Provides a unique identifier for the JWT.
     * Often used to prevent token replay attacks by ensuring each token is used only once,
     * or blacklisting the leaked tokens.
     */
    public static final String JWT_ID = "jti";
}
