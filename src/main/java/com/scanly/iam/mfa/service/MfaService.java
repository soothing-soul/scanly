package com.scanly.iam.mfa.service;

import com.scanly.iam.auth.common.model.AuthMethodType;
import com.scanly.iam.mfa.model.MfaConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class serves as a placeholder for the MFA logic that will be incorporated in
 * the scanly application in the future.
 */
@Service
public class MfaService {
    public MfaConfig getMfaConfig(UUID userId) {
        return new MfaConfig(
                true,
                new ArrayList<>(List.of(AuthMethodType.PASSWORD, AuthMethodType.EMAIL_OTP))
        );
    }
}
