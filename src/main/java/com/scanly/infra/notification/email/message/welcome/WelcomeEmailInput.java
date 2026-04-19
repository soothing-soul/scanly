package com.scanly.infra.notification.email.message.welcome;

import com.scanly.infra.notification.email.message.EmailContentInput;
import com.scanly.infra.notification.email.message.EmailContentMapper;

/**
 * Data input record for generating "Welcome" email content for new users.
 * <p>
 * This record implements {@link EmailContentInput} and carries the personal
 * information required to greet a user upon successful registration. It is
 * typically processed by an {@link EmailContentMapper} to populate the "welcome"
 * template with the user's name.
 * </p>
 *
 * @param name The name of the user to be greeted in the email (e.g., "John Doe").
 * @author Scanly Infrastructure Team
 */
public record WelcomeEmailInput(
        String name
) implements EmailContentInput {}
