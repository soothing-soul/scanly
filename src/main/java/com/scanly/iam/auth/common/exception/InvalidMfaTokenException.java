package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when the Multi-Factor Authentication (MFA) token provided
 * in the request is invalid, tampered with, or logically mismatched.
 * <p>
 * In the Scanly IAM flow, an {@code mfaToken} is issued after a successful primary
 * authentication and must be presented to perform subsequent MFA actions. This
 * exception is triggered if:
 * <ul>
 * <li>The token signature is invalid (suggesting tampering).</li>
 * <li>The token has been revoked or blacklisted.</li>
 * <li>The token does not match the {@code flowId} provided in the same request.</li>
 * <li>The token has been used more than once (replay protection).</li>
 * </ul>
 * </p>
 * <p>
 * <b>Note:</b> This is distinct from {@link AuthSessionExpiredException}, which
 * focuses on the <i>temporal</i> expiration of the session rather than the
 * <i>integrity</i> of the token itself.
 * </p>
 *
 * @see AuthError#INVALID_MFA_TOKEN
 */
public class InvalidMfaTokenException extends AuthException {

    /** The constant error mapping that binds this exception to a 401 Unauthorized status. */
    private static final AuthError error = AuthError.INVALID_MFA_TOKEN;

    /**
     * Constructs a new InvalidMfaTokenException with a custom explanation.
     *
     * @param message A detail message explaining why the token was rejected.
     */
    public InvalidMfaTokenException(String message) {
        super(message, error);
    }
}