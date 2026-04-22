package com.scanly.iam.auth.identification;

import com.scanly.iam.auth.common.dto.response.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for the initial identification phase of the
 * authentication process.
 * <p>
 * This controller handles the first "handshake" between a client and the IAM system.
 * Its primary responsibility is to receive a user's identifier (email) and
 * delegate the logic to determine the next required step in the authentication
 * sequence.
 * </p>
 */
@RestController
public class IdentificationAuthController {

    private final IdentificationAuthHandler identificationAuthHandler;

    /**
     * Constructs the controller with the required identification handler.
     *
     * @param identificationAuthHandler The handler managing identity resolution logic.
     */
    public IdentificationAuthController(IdentificationAuthHandler identificationAuthHandler) {
        this.identificationAuthHandler = identificationAuthHandler;
    }

    /**
     * Initiates an authentication session by identifying the user.
     * <p>
     * This endpoint receives an email address, validates it, and triggers the
     * creation of a new authentication flow. The response will dictate whether
     * the user needs to provide a password, an MFA token, or if the account
     * is currently restricted.
     * </p>
     *
     * @param authInitiateRequest The request body containing the user's email.
     * @return An {@link AuthResponse} containing the {@code flowId} and the
     * metadata for the next required step.
     */
    @PostMapping("/api/v1/auth/identify")
    public AuthResponse identify(
            @RequestBody @Valid AuthInitiateRequest authInitiateRequest
    ) {
        return identificationAuthHandler.identify(authInitiateRequest);
    }
}