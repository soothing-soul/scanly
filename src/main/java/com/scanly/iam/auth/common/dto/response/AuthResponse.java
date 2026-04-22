package com.scanly.iam.auth.common.dto.response;

/**
 * Marker interface for all authentication-related response Data Transfer Objects (DTOs).
 * <p>
 * This interface serves as a common high-level type for the authentication API,
 * allowing different types of responses (e.g., {@code LoginResponse}, {@code TokenResponse},
 * or {@code MfaRequiredResponse}) to be handled by generic components or specialized
 * response wrappers.
 * </p>
 */
public interface AuthResponse {
    // This is a marker interface; no methods are defined here.
}