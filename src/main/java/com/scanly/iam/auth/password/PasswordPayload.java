package com.scanly.iam.auth.password;

import jakarta.validation.constraints.NotBlank;

/**
 * A specialized payload containing the user's password credential.
 * <p>
 * This record is used as the generic type within a {@code StepAuthRequest}
 * when the authentication flow reaches the password verification stage.
 * </p>
 * <p>
 * <b>Security Note:</b> Because this is a {@code record}, the internal
 * {@code password} field is final and immutable. While this is good for
 * thread safety, the underlying {@code String} remains in the JVM's
 * string pool until garbage collected. In highly sensitive environments,
 * some developers prefer {@code char[]} to allow for manual clearing,
 * but for most modern web applications, the simplicity of {@code String}
 * combined with TLS and short-lived request scopes is the industry standard.
 * </p>
 *
 * @param password The raw password string provided by the user.
 */
public record PasswordPayload(
        @NotBlank String password
) {}