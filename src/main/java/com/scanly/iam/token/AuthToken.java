package com.scanly.iam.token;

import com.scanly.iam.token.accesstoken.AccessToken;

/**
 * A composite domain record representing the complete set of authentication credentials
 * issued to a user upon a successful login or token refresh event.
 * <p>
 * Within the Scanly IAM architecture, this record acts as the "Authentication Bundle."
 * While the internal {@link AccessToken} defines the credential for immediate resource
 * access, this top-level container provides a unified response structure that can be
 * serialized and returned by the {@code AuthTokenIssuer} orchestrator.
 * <p>
 * <b>Architectural Significance:</b> By wrapping specific token types in this composite
 * record, we decouple the public-facing authentication API from the internal complexity
 * of managing multiple token lifecycles.
 *
 * @param accessToken The primary, short-lived credential used for authorizing
 * individual HTTP requests against protected resources.
 */
public record AuthToken(
        /*
         * Current Implementation Note: This record is structured as a holder.
         * As the system evolves, additional fields (e.g., Refresh Tokens or
         * session metadata) will be aggregated here without breaking the
         * return type contract of the orchestrator.
         */
        AccessToken accessToken
) {
}