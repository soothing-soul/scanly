package com.scanly.iam.auth.password;

import com.scanly.iam.auth.common.dto.request.StepAuthRequest;
import com.scanly.iam.auth.common.dto.response.AuthResponse;
import com.scanly.iam.auth.common.service.AuthHandler;
import com.scanly.iam.auth.common.service.StepExecutor;
import org.springframework.stereotype.Service;

/**
 * Service handler that coordinates password verification by delegating to the
 * central authentication orchestrator.
 * <p>
 * This class serves as the specific entry point for the password-verification
 * phase. It pairs the incoming {@link StepAuthRequest} with the
 * {@link PasswordStepExecutor} and passes them to the {@link AuthHandler}
 * to ensure the operation is executed within a secure, synchronized, and
 * flow-aware context.
 * </p>
 */
@Service
public class PasswordAuthHandler {

    private final AuthHandler authHandler;
    private final StepExecutor<PasswordPayload> passwordStepExecutor;

    /**
     * Constructs the PasswordAuthHandler.
     *
     * @param authHandler          The central orchestrator for auth step execution.
     * @param passwordStepExecutor The strategy implementation for checking passwords.
     */
    public PasswordAuthHandler(AuthHandler authHandler, StepExecutor<PasswordPayload> passwordStepExecutor) {
        this.authHandler = authHandler;
        this.passwordStepExecutor = passwordStepExecutor;
    }

    /**
     * Initiates the synchronized verification of a password payload.
     * <p>
     * By calling {@code authHandler.handle()}, this method ensures that:
     * <ul>
     * <li>A distributed lock is acquired for the user.</li>
     * <li>The password is validated via the {@code passwordStepExecutor}.</li>
     * <li>The authentication flow state is advanced upon success.</li>
     * </ul>
     * </p>
     *
     * @param request The request containing the flow ID and the password payload.
     * @return An {@link AuthResponse} indicating the next step in the sequence.
     */
    public AuthResponse verify(StepAuthRequest<PasswordPayload> request) {
        return authHandler.handle(request, passwordStepExecutor);
    }
}