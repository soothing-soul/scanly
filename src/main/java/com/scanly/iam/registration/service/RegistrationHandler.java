package com.scanly.iam.registration.service;

import com.scanly.iam.common.EmailNormalizer;
import com.scanly.iam.policy.email.EmailPolicyValidator;
import com.scanly.iam.policy.email.model.EmailContext;
import com.scanly.iam.policy.exception.EmailPolicyValidationException;
import com.scanly.iam.policy.exception.PasswordPolicyValidationException;
import com.scanly.iam.policy.password.PasswordPolicyValidator;
import com.scanly.iam.policy.password.model.PasswordContext;
import com.scanly.iam.policy.password.model.PasswordOperation;
import com.scanly.iam.registration.web.RegistrationRequest;
import com.scanly.iam.registration.web.RegistrationResponse;
import com.scanly.iam.user.domain.User;
import com.scanly.iam.user.exception.EmailAlreadyExistsException;
import org.springframework.stereotype.Service;

/**
 * This class acts as the high-level coordinator for the user registration
 * process.
 * <p>
 * This handler is responsible for the orchestration of pre-persistence logic,
 * including data normalization and policy validation. It acts as a protective
 * layer for the {@link RegistrationPersistenceService}, ensuring that only
 * valid and compliant data reaches the database.
 * </p>
 */
@Service
public class RegistrationHandler {

    private final EmailPolicyValidator emailPolicyValidator;
    private final PasswordPolicyValidator passwordPolicyValidator;
    private final RegistrationPersistenceService registrationPersistenceService;

    /**
     * Constructs the handler with necessary validators and the persistence coordinator.
     * @param emailPolicyValidator Validator for organizational or technical email rules.
     * @param passwordPolicyValidator Validator for security requirements (complexity, length, etc.).
     * @param registrationPersistenceService Service for atomic database operations.
     */
    public RegistrationHandler(
            EmailPolicyValidator emailPolicyValidator,
            PasswordPolicyValidator passwordPolicyValidator,
            RegistrationPersistenceService registrationPersistenceService
    ) {
        this.emailPolicyValidator = emailPolicyValidator;
        this.passwordPolicyValidator = passwordPolicyValidator;
        this.registrationPersistenceService = registrationPersistenceService;
    }

    /**
     * Handles the end-to-end registration request workflow.
     * <p>
     * <b>Execution Steps:</b>
     * <ol>
     * <li><b>Normalization:</b> Sanitizes the input email to a canonical form (e.g., lowercase).</li>
     * <li><b>Email Validation:</b> Checks against policies (e.g., blacklisted domains).</li>
     * <li><b>Password Validation:</b> Evaluates the password against security rules
     * using context-aware checks (e.g., ensuring password doesn't contain the email).</li>
     * <li><b>Persistence:</b> Triggers the atomic creation of the User and Credentials.</li>
     * <li><b>Response Generation: </b> Returns the final success response to the user </li>
     * </ol>
     * </p>
     *
     * @param registrationRequest The raw registration payload from the web layer.
     * @return A {@link RegistrationResponse} containing the generated identity details.
     * @throws EmailPolicyValidationException if the email is invalid as per the policy
     * @throws PasswordPolicyValidationException if the password is weak.
     * @throws EmailAlreadyExistsException if the email is already in use.
     */
    public RegistrationResponse handle(RegistrationRequest registrationRequest) {
        // Step 1: Canonicalize input to prevent duplicates like User@Example.com vs user@example.com
        String normalizedEmail = EmailNormalizer.normalize(registrationRequest.email());

        // Step 2: Validate Email Policy
        emailPolicyValidator.validate(new EmailContext(normalizedEmail));

        // Step 3: Validate Password Policy (Context-aware: checks if password matches email, etc.)
        passwordPolicyValidator.validate(new PasswordContext(
                registrationRequest.password(),
                normalizedEmail,
                null,
                PasswordOperation.REGISTRATION
        ));

        // Step 4: Atomic Persistence
        User user = registrationPersistenceService.register(new RegistrationRequest(
                normalizedEmail,
                registrationRequest.password()
        ));

        // Step 5: Final response
        return new RegistrationResponse(
                user.userId(),
                user.email()
        );
    }
}