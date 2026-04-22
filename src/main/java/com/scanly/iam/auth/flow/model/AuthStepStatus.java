package com.scanly.iam.auth.flow.model;

/**
 * Represents the current state of an individual authentication step within
 * an authentication flow.
 * <p>
 * These statuses allow the state machine to track progress at a granular level,
 * ensuring that actions (like sending an OTP) are only performed when
 * appropriate and that verification only occurs after a challenge has
 * been issued.
 * </p>
 */
public enum AuthStepStatus {

    /**
     * The step has been initialized or identified as a requirement, but no
     * action has been taken yet.
     * <p>
     * Example: The system knows the user needs to provide a password, but
     * the password field hasn't been submitted.
     * </p>
     */
    PENDING,

    /**
     * An active challenge has been issued to the user, and the system is
     * awaiting a response.
     * <p>
     * Example: An SMS OTP has been generated and sent; the system is now
     * waiting for the user to enter the code.
     * </p>
     */
    CHALLENGED,

    /**
     * The user has successfully completed the challenge for this step.
     * <p>
     * Example: The password was correct, or the TOTP code was verified.
     * Once a step is {@code VERIFIED}, the flow can proceed to the next
     * required factor or complete the login.
     * </p>
     */
    VERIFIED
}