package com.scanly.crypto.model;

/**
 * Defines the cryptographic lifecycle states for keys within the Scanly platform.
 * <p>
 * This enum governs how keys are transitioned from active duty to eventual
 * decommissioning. It manages three distinct capabilities:
 * <ol>
 * <li><b>Availability:</b> Eligibility for selection during new token generation.</li>
 * <li><b>Signature:</b> Permission to use the associated Private Key for signing.</li>
 * <li><b>Verification:</b> Permission to use the Public Key to validate existing signatures.</li>
 * </ol>
 * </p>
 */
public enum KeyStatus {
    /**
     * The primary key state. Fully available for all cryptographic operations
     * and eligible for selection by encoders.
     */
    ACTIVE,

    /**
     * A transitional state for keys being rotated out.
     * <p>
     * The key is no longer available for new token requests but remains
     * valid for signing operations that are already in-flight or for
     * specific legacy requirements.
     * </p>
     */
    RETIRING,

    /**
     * A state for keys that have been superseded for signing but must remain
     * for signature validation of unexpired tokens or historical data.
     */
    VERIFY_ONLY,

    /**
     * The final state for a key. It is effectively "dead" for the purpose
     * of new business logic, though its public key remains accessible for
     * deep-forensic verification if necessary.
     */
    EXPIRED;

    /**
     * @return true if the status is ACTIVE.
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * Determines if the private key associated with this status can be used
     * to generate a signature.
     * @return true if the status is ACTIVE or RETIRING.
     */
    public boolean canSign() {
        return this == ACTIVE || this == RETIRING;
    }

    /**
     * Determines if the public key associated with this status can be used
     * to verify a signature.
     * <p>
     * Note: In this implementation, EXPIRED keys still allow verification
     * to support historical data audits.
     * </p>
     * @return true if the status is ACTIVE, RETIRING, or EXPIRED.
     */
    public boolean canVerify() {
        // Includes EXPIRED to allow for historical cryptographic proof
        return this == ACTIVE || this == RETIRING || this == EXPIRED || this == VERIFY_ONLY;
    }
}