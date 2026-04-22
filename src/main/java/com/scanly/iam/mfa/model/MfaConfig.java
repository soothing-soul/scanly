package com.scanly.iam.mfa.model;

import com.scanly.iam.auth.common.model.AuthMethodType;

import java.util.List;

/**
 * This record serves as a placeholder to communicate the MFA configuration of
 * a user.
 * @param mfaEnabled
 * @param enabledMethods
 */
public record MfaConfig(
        boolean mfaEnabled,
        List<AuthMethodType> enabledMethods
) {}
