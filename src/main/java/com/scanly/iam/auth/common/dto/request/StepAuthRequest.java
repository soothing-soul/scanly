package com.scanly.iam.auth.common.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * A generic data transfer object used to submit credentials or actions
 * during a specific step of the authentication process.
 * <p>
 * This request carries the stateful identifiers (flowId and mfaToken) along with
 * the specific payload required to satisfy the current authentication challenge.
 * </p>
 *
 * @param <T>               The type of the payload, allowing for flexible data
 *                          structures depending on the authentication method.
 * @param flowId            The unique identifier for the ongoing authentication session.
 * @param mfaToken          The temporary token issued by the previous response to
 *                          authorize this specific step.
 * @param authMethod        The authentication strategy being used (e.g., "TOTP",
 *                          "SMS_OTP", "SECURITY_QUESTION").
 * @param authAction        The specific action being performed (e.g., "VERIFY",
 *                          "RESEND_CODE", "CANCEL").
 * @param payload           The actual credential data or input required for this step.
 */
public record StepAuthRequest<T>(
        @NotBlank String flowId,
        @Nullable String mfaToken,
        @NotBlank String authMethod,
        @NotBlank String authAction,
        @Valid T payload
) {}