package com.scanly.iam.auth.password;

import com.scanly.iam.auth.common.exception.InvalidCredentialsException;
import com.scanly.iam.auth.common.model.StepContext;
import com.scanly.iam.auth.common.service.StepExecutor;
import com.scanly.iam.credential.service.CredentialVerificationService;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link StepExecutor} for password-based authentication.
 * <p>
 * This component acts as the bridge between the high-level authentication orchestrator
 * and the low-level credential verification domain. It ensures that the "Knowledge"
 * factor is correctly validated before allowing an authentication flow to proceed.
 * </p>
 */
@Component
public class PasswordStepExecutor implements StepExecutor<PasswordPayload> {

    private final CredentialVerificationService credentialVerificationService;

    /**
     * Constructs the PasswordStepExecutor with its required domain service.
     *
     * @param credentialVerificationService The service responsible for password
     * hashing and comparison logic.
     */
    public PasswordStepExecutor(CredentialVerificationService credentialVerificationService) {
        this.credentialVerificationService = credentialVerificationService;
    }

    /**
     * Validates the provided password against the user's stored credentials.
     * <p>
     * If the {@link CredentialVerificationService} returns {@code false}, this method
     * throws {@link InvalidCredentialsException}.
     * </p>
     *
     * <p>
     *     <b>Security Note:</b> In the future, there will be a {@code SecurityService}
     *     dedicated completely to block the malicious attempts by keeping track of the
     *     failed attempts for {@code password} or {@code otp} based on various policies.
     * </p>
     *
     * @param payload     The {@link PasswordPayload} containing the user-submitted password.
     * @param stepContext The context providing the {@code userId} for identity resolution.
     * @throws InvalidCredentialsException if the password verification fails.
     */
    @Override
    public void execute(PasswordPayload payload, StepContext stepContext) {
        // Delegate the cryptographic comparison to the specialized domain service
        boolean matches = credentialVerificationService.verify(
                stepContext.userId(), payload.password()
        );

        if (!matches) {
            throw new InvalidCredentialsException(
                    String.format("Invalid credentials for userId: %s", stepContext.userId())
            );
        }
    }
}