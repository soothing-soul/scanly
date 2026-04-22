package com.scanly.iam.auth.identification;

import com.scanly.iam.auth.common.dto.response.AuthResponse;
import com.scanly.iam.auth.flow.AuthFlowService;
import com.scanly.iam.common.EmailNormalizer;
import org.springframework.stereotype.Service;

/**
 * Service handler responsible for processing the initial identity lookup.
 * <p>
 * This class serves as the entry point for the authentication business logic.
 * It ensures that identifiers are standardized (normalized) before querying
 * the identity provider or initializing an authentication flow, preventing
 * duplicate sessions based on casing or formatting variations.
 * </p>
 */
@Service
public class IdentificationAuthHandler {

    private final AuthFlowService authFlowService;

    /**
     * Constructs the IdentificationAuthHandler.
     *
     * @param authFlowService The service managing the creation and tracking
     * of authentication flows.
     */
    public IdentificationAuthHandler(AuthFlowService authFlowService) {
        this.authFlowService = authFlowService;
    }

    /**
     * Standardizes the user's identifier and initiates a new authentication flow.
     * <p>
     * The process involves:
     * <ol>
     * <li>Normalizing the email (e.g., converting to lowercase, trimming whitespace).</li>
     * <li>Delegating to the {@link AuthFlowService} to determine if the user exists
     * and what the first required security challenge is.</li>
     * </ol>
     * </p>
     *
     * @param authInitiateRequest The request object containing the user's raw email.
     * @return An {@link AuthResponse} containing the initial flow state and next steps.
     */
    public AuthResponse identify(AuthInitiateRequest authInitiateRequest) {
        String email = authInitiateRequest.email();

        // Ensure 'User@Example.com' and 'user@example.com' are treated as the same identity.
        String normalizedEmail = EmailNormalizer.normalize(email);

        // Handoff to the flow service to create the session and return the first challenge.
        return authFlowService.initiate(normalizedEmail);
    }
}