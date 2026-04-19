package com.scanly.infra.notification.email.message.otp;

import com.scanly.infra.notification.email.message.EmailContentInput;
import com.scanly.infra.notification.email.message.EmailContentMapper;

import java.time.Duration;

/**
 * Data input record for generating One-Time Password (OTP) email content.
 * <p>
 * This record implements {@link EmailContentInput} and carries the specific
 * data required to populate an OTP email template. It is typically processed
 * by a specialized {@link EmailContentMapper} to transform these fields into
 * a map of strings for the template engine.
 * </p>
 *
 * @param otp     The actual one-time password string to be delivered to the user.
 * @param expiry  The time duration for which the OTP is considered valid.
 * @param purpose The {@link OtpPurpose} (e.g., LOGIN, PASSWORD_RESET) which allows
 * the template to provide specific context to the recipient.
 * @author Scanly Infrastructure Team
 */
public record OtpEmailInput(
        String otp,
        Duration expiry,
        OtpPurpose purpose
) implements EmailContentInput {}
