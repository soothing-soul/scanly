package com.scanly.iam.registration.service;

import com.scanly.iam.credential.service.CredentialCreationService;
import com.scanly.iam.registration.web.RegistrationRequest;
import com.scanly.iam.user.domain.User;
import com.scanly.iam.user.exception.EmailAlreadyExistsException;
import com.scanly.iam.user.service.UserCreationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrator service responsible for the atomic persistence of new user identities
 * and their associated security credentials.
 * <p>
 * This service acts as a transaction boundary, ensuring that the creation of a
 * {@link User} and their {@code Credential} are treated as a single unit of work.
 * By centralizing these calls, the system guarantees that no "orphaned" identities
 * (users without passwords) are created in the event of a failure.
 * </p>
 */
@Service
public class RegistrationPersistenceService {

    private final UserCreationService userCreationService;
    private final CredentialCreationService credentialCreationService;

    /**
     * Constructs the persistence service with required identity and credential providers.
     * @param userCreationService The service responsible for identity lifecycle management.
     * @param credentialCreationService The service responsible for password hashing and validation.
     */
    public RegistrationPersistenceService(UserCreationService userCreationService,
                                          CredentialCreationService credentialCreationService
    ) {
        this.userCreationService = userCreationService;
        this.credentialCreationService = credentialCreationService;
    }

    /**
     * Executes the registration workflow within a managed database transaction.
     * <p>
     * <b>Operational Flow:</b>
     * <ol>
     * <li>Creates a core User record to establish a unique {@code userId}.</li>
     * <li>Passes the generated ID to the credential service to link a hashed password.</li>
     * <li>Returns the fully initialized domain object.</li>
     * </ol>
     * </p>
     * <p>
     * <b>Rollback Policy:</b>
     * If {@code credentialCreationService} fails (e.g., due to password policy violations),
     * the {@code userCreationService} insert is rolled back automatically.
     * </p>
     *
     * @param registrationRequest The incoming registration DTO containing email and raw password.
     * @return The {@link User} domain object representing the newly created identity.
     * @throws EmailAlreadyExistsException if the email is already registered.
     */
    @Transactional
    public User register(RegistrationRequest registrationRequest) {
        // Step 1: Establish Identity
        User user = userCreationService.create(registrationRequest.email());

        // Step 2: Establish Security Credential
        credentialCreationService.create(
                user.userId(),
                registrationRequest.password()
        );
        return user;
    }
}