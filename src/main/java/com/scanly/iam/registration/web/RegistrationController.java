package com.scanly.iam.registration.web;

import com.scanly.iam.registration.service.RegistrationHandler;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public API Controller for user registration.
 * <p>
 * This controller exposes the endpoint required for new users to join the platform.
 * It handles the transformation of incoming JSON payloads into structured
 * request objects and ensures basic JSR-303 validation is met before
 * processing the registration.
 * </p>
 */
@RestController
public class RegistrationController {

    /** The service responsible for orchestrating the multi-step registration process. */
    private final RegistrationHandler registrationHandler;

    /**
     * Constructs the controller with the required registration handler.
     *
     * @param registrationHandler The business logic coordinator for registrations.
     */
    public RegistrationController(RegistrationHandler registrationHandler) {
        this.registrationHandler = registrationHandler;
    }

    /**
     * Handles the HTTP POST request to register a new user.
     * <p>
     * Endpoint: {@code POST /api/v1/auth/register}
     * <p>
     * This method triggers the standard Spring validation on the {@link RegistrationRequest}.
     * If the request body is syntactically valid, it passes control to the
     * {@link RegistrationHandler} to perform policy checks and data persistence.
     *
     * @param registrationRequest The validated user registration data (email, password, etc.).
     * @return A {@link RegistrationResponse} containing the new user's metadata (e.g., UUID).
     */
    @PostMapping("/api/v1/auth/register")
    public RegistrationResponse registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) {
        return registrationHandler.handle(registrationRequest);
    }
}