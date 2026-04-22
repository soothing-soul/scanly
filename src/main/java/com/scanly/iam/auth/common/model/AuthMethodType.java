package com.scanly.iam.auth.common.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the authentication methods supported by the Scanly IAM system and their
 * operational capabilities.
 * <p>
 * This enum maps each method to an {@link AuthFactorType} and defines which
 * {@link AuthAction}s (GENERATE/VERIFY) it supports. It also encapsulates
 * business rules for flow eligibility, such as which methods can initiate an
 * MFA sequence or act as a standalone login factor.
 * </p>
 */
public enum AuthMethodType {

    /**
     * Knowledge-based authentication using a secret string.
     * Usually the primary step in multifactor flows.
     */
    PASSWORD(
            AuthFactorType.KNOWLEDGE,
            List.of(AuthAction.VERIFY)
    ),

    /**
     * Possession-based authentication using OTP sent on EMAIL.
     */
    EMAIL_OTP(
            AuthFactorType.POSSESSION,
            List.of(AuthAction.GENERATE, AuthAction.VERIFY)
    );


    /** The factor category (Knowledge, Possession, Inheritance) this method belongs to. */
    private final AuthFactorType authFactorType;

    /** The actions this method is capable of performing within a flow. */
    private final List<AuthAction> supportedActions;

    /**
     * Internal constructor to bind factor types and capabilities to a method.
     */
    AuthMethodType(AuthFactorType authFactorType, List<AuthAction> supportedActions) {
        this.authFactorType = authFactorType;
        this.supportedActions = supportedActions;
    }

    /**
     * @return The {@link AuthFactorType} associated with this method.
     */
    public AuthFactorType getAuthFactorType() {
        return authFactorType;
    }

    /**
     * Determines if this method is secure enough to allow a full login without
     * additional factors.
     * @return {@code true} if allowed for single-factor flows.
     */
    public boolean isEligibleForSingleFactorLogin() {
        List<AuthMethodType> methodsAllowedForSingleFactorLogin
                = new ArrayList<>(List.of(
                        AuthMethodType.PASSWORD
                ));
        return methodsAllowedForSingleFactorLogin.contains(this);
    }

    /**
     * Checks if this method can serve as the first "gate" in an MFA process.
     * Typically restricted to {@link AuthFactorType#KNOWLEDGE}.
     * @return {@code true} if eligible for the first step.
     */
    public boolean isEligibleForFirstStepInMfaLogin() {
        return this.authFactorType == AuthFactorType.KNOWLEDGE;
    }

    /**
     * Checks if this method can serve as a second-factor challenge.
     * Typically restricted to {@link AuthFactorType#POSSESSION}.
     * @return {@code true} if eligible for an MFA challenge step.
     */
    public boolean isEligibleForMfaStep() {
        return this.authFactorType == AuthFactorType.POSSESSION;
    }

    /**
     * Validates whether a specific action is logically sound for this method.
     * (e.g., You cannot "GENERATE" a TOTP code on the server side).
     *
     * @param action The {@link AuthAction} to check.
     * @return {@code true} if the action is supported.
     */
    public boolean isActionSupported(AuthAction action) {
        return supportedActions.contains(action);
    }

    /**
     * Checks if {@link AuthAction#GENERATE} is a prerequisite of {@link AuthAction#VERIFY}
     * for the current auth method.
     * @return
     */
    public boolean isGenerationRequired() {
        return supportedActions.contains(AuthAction.GENERATE);
    }
}