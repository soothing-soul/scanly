package com.scanly.infra.notification.email.message;

import com.scanly.infra.notification.email.model.EmailTemplateType;

/**
 * A marker interface representing the data input for an email template.
 * <p>
 * This interface is used to define type-safe data structures that carry the
 * information required to populate specific {@link EmailTemplateType}s.
 * </p>
 * <p>
 * Implementing this interface allows the notification system to move away from
 * generic {@code Map<String, String>} structures toward specific, validated
 * data objects (e.g., {@code OtpEmailInput}, {@code WelcomeEmailInput}).
 * </p>
 */
public interface EmailContentInput {
}