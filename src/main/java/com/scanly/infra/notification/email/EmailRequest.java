package com.scanly.infra.notification.email;

/**
 * An immutable data transfer object (DTO) representing a request to send an email.
 * <p>
 * This record uses Java Generics to allow for flexible, type-safe data payloads.
 * The {@code payload} contained herein is intended to be consumed by an
 * {@link EmailBodyBuilder} mapped to the specified {@link EmailPurpose}.
 * </p>
 *
 * @param <T>          The type of the data payload required by the specific email template.
 * @param toAddress    The destination email address of the recipient.
 * @param subject      The specific subject line for this email instance.
 * @param emailPurpose The functional category of the email, used for strategy resolution.
 * @param payload      The data object containing variables for the email body (e.g., OTP codes, user names).
 * @see EmailPurpose
 * @see MailService
 */
public record EmailRequest<T>(
        String toAddress,
        String subject,
        EmailPurpose emailPurpose,
        T payload
) {}