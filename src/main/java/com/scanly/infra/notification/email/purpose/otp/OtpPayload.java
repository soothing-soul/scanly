package com.scanly.infra.notification.email.purpose.otp;

import com.scanly.infra.notification.email.EmailPurpose;
import com.scanly.infra.notification.email.EmailRequest;

import java.time.Duration;

/**
 * A data transfer object (DTO) representing the information required to
 * construct an OTP-based email.
 * <p>
 * This record is intended to be used as the payload in an {@link EmailRequest}
 * when the purpose is {@link EmailPurpose#OTP}.
 * </p>
 *
 * @param otp     The actual verification code to be sent to the user.
 * @param purpose The context of the request (e.g., "Login", "Account Recovery").
 * Used to customize the email's messaging.
 * @param expiry  The duration for which the OTP remains valid. Usually formatted
 * into human-readable text (e.g., "10 minutes") within the email body.
 */
public record OtpPayload(
        String otp,
        String purpose,
        Duration expiry
) {}