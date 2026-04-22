package com.scanly.iam.auth.email;

import com.scanly.iam.auth.common.dto.request.StepAuthRequest;
import com.scanly.iam.auth.common.dto.response.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller providing endpoints for the Email OTP authentication flow.
 * <p>
 * This controller serves as the entry point for users to request and verify
 * one-time passwords delivered via email. It is part of the IAM (Identity and
 * Access Management) domain and coordinates with the {@link EmailOtpAuthHandler}
 * to execute the multistep authentication process.
 * <p>
 */
@RestController
public class EmailOtpAuthController {

    private final EmailOtpAuthHandler emailOtpAuthHandler;

    /**
     * Constructs the controller with the necessary domain handler.
     *
     * @param emailOtpAuthHandler The component responsible for orchestrating
     * the IAM-specific logic for email OTP challenges.
     */
    public EmailOtpAuthController(EmailOtpAuthHandler emailOtpAuthHandler) {
        this.emailOtpAuthHandler = emailOtpAuthHandler;
    }

    /**
     * Triggers the generation and delivery of a new OTP to the user's email address.
     *
     * <p>
     * This endpoint is called at the start of an email OTP authentication flow. The
     * handler will utilize the internal OTP service to create the challenge and
     * then dispatch the code via the notification infrastructure.
     * </p>
     *
     * @param stepAuthRequest Encapsulated payload containing the user
     * identification or context needed to generate the OTP.
     * @return An {@link AuthResponse} indicating the success of the trigger
     * and any subsequent steps required.
     */
    @PostMapping("/api/v1/auth/email-otp/send")
    public AuthResponse generate(@Valid @RequestBody StepAuthRequest<EmailOtpGenerationPayload> stepAuthRequest) {
        return emailOtpAuthHandler.generate(stepAuthRequest);
    }

    /**
     * Validates a user-provided OTP against an active challenge.
     * <p>
     * This endpoint completes the authentication "step." Based on the result
     * from the handler, this may result in a full authentication token,
     * a transition to the next MFA step, or a rejection due to invalid input
     * or maximum attempts reached.
     * </p>
     *
     * @param stepAuthRequest Encapsulated payload containing the raw OTP
     * and the correlation ID (challengeId).
     * @return An {@link AuthResponse} representing the final authentication
     * state or the next required action.
     */
    @PostMapping("/api/v1/auth/email-otp/verify")
    public AuthResponse verify(@RequestBody StepAuthRequest<EmailOtpVerificationPayload> stepAuthRequest) {
        return emailOtpAuthHandler.verify(stepAuthRequest);
    }
}