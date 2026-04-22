package com.scanly.iam.auth.common.dto.response;

import java.util.List;

/**
 * Represents a partial authentication state, typically returned when Multi-Factor
 * Authentication (MFA) or an additional verification step is required.
 * <p>
 * This DTO provides the client with the necessary metadata to continue the
 * authentication process, including the session identifier and available
 * verification challenges.
 * </p>
 *
 * @param flowId                 The unique identifier for the current authentication session.
 *                               Used to maintain state across multiple requests.
 * @param mfaToken               A short-lived, restricted token required to authorize
 *                               the next step in the authentication challenge.
 * @param authenticationRequired A flag indicating if the user still needs to provide
 *                               further credentials before being fully authenticated.
 * @param availableMethods       A list of authorized MFA methods the user can choose from
 *                               (e.g., "SMS", "TOTP", "EMAIL").
 */
public record StepAuthResponse(
        String flowId,
        String mfaToken,
        boolean authenticationRequired,
        List<String> availableMethods
) implements AuthResponse {}