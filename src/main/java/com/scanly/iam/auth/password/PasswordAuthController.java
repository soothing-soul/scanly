package com.scanly.iam.auth.password;

import com.scanly.iam.auth.common.dto.request.StepAuthRequest;
import com.scanly.iam.auth.common.dto.response.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling password-based authentication steps.
 * <p>
 * This controller is invoked when the authentication flow requires a
 * "Something you know" (Knowledge) factor. It processes the user's password
 * submission within the context of an existing authentication flow.
 * </p>
 */
@RestController
public class PasswordAuthController {

    private final PasswordAuthHandler passwordAuthHandler;

    /**
     * Constructs the PasswordAuthController with its corresponding handler.
     *
     * @param passwordAuthHandler The handler responsible for password verification logic.
     */
    public PasswordAuthController(PasswordAuthHandler passwordAuthHandler) {
        this.passwordAuthHandler = passwordAuthHandler;
    }

    /**
     * Verifies the provided password against the stored credentials for the user
     * associated with the current flow.
     * <p>
     * Upon success, this endpoint typically advances the flow to the next step
     * (e.g., an MFA challenge) or completes the authentication process.
     * </p>
     *
     * @param request A {@link StepAuthRequest} containing the {@code flowId},
     * {@code mfaToken}, and the {@link PasswordPayload}.
     * @return An {@link AuthResponse} indicating the next requirement or the final
     * authentication success state.
     */
    @PostMapping("/api/v1/auth/password/verify")
    public AuthResponse verifyPassword(
            @RequestBody @Valid StepAuthRequest<PasswordPayload> request
    ) {
        return passwordAuthHandler.verify(request);
    }
}