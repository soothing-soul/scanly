package com.scanly.iam.auth.common.model;

/**
 * Defines the specific operations that can be performed during an
 * authentication step.
 * <p>
 * These actions inform the backend whether it should initiate a new
 * challenge (like sending an SMS) or validate a user-provided response
 * (like checking a TOTP code).
 * </p>
 */
public enum AuthAction {

    /**
     * Requests the system to produce and deliver a challenge to the user.
     * <p>
     * Use this action when a user needs to receive a new One-Time Password (OTP)
     * via SMS or Email, or when a unique cryptographic challenge needs to
     * be generated for WebAuthn.
     * </p>
     */
    GENERATE,

    /**
     * Submits a user-provided credential or response for validation.
     * <p>
     * Use this action when the user has entered their code, password,
     * or biometric data and is attempting to satisfy the current
     * authentication requirement.
     * </p>
     */
    VERIFY
}