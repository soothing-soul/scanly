package com.scanly.iam.auth.flow;

import com.scanly.iam.auth.common.model.AuthMethodType;
import com.scanly.iam.auth.flow.model.AuthStep;
import com.scanly.iam.mfa.model.MfaConfig;
import com.scanly.iam.mfa.service.MfaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for determining the sequence of authentication challenges
 * required for a specific user.
 * <p>
 * This resolver acts as the policy orchestrator of the IAM system. It evaluates the
 * user's {@link MfaConfig} and organizes available {@link AuthMethodType}s into a
 * logical progression of steps, ensuring that security requirements
 * (like Knowledge + Possession) are met before a session is granted.
 * </p>
 *
 * <p>
 *     <b>Note:</b> In the future, AuthPolicy or RiskAssessmentService can be plugged
 *     in here to add extra authentication steps based on the policy or the risk involved.
 * </p>
 *
 */
@Service
public class AuthStepsResolver {

    private final MfaService mfaService;

    public AuthStepsResolver(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    /**
     * Resolves the required authentication steps for a user based on their security settings.
     * <p>
     * <b>Logic:</b>
     * <ul>
     * <li>If MFA is <b>disabled</b>: Returns a single step containing all methods
     * eligible for single-factor entry (e.g., OTP-only or Password-only).</li>
     * <li>If MFA is <b>enabled</b>: Returns a two-step sequence.
     * <ul>
     *     <li>
     *         Step 1 focuses on "Knowledge" (Something you know)
     *
     *     </li>
     *     <li>
     *         Step 2 focuses on "Possession" (Something you have)
     *     </li>
     * </ul>
     * </li>
     * </ul>
     * </p>
     *
     * @param userId The unique identifier of the user.
     * @return A sequential list of {@link AuthStep} objects defining the login roadmap.
     */
    public List<AuthStep> resolveSteps(UUID userId) {
        MfaConfig mfaConfig = mfaService.getMfaConfig(userId);

        if (isMfaRequired(mfaConfig)) {
            return getAuthStepsForMultiFactor(mfaConfig);
        } else {
            return getAuthStepsForSingleFactor(mfaConfig);
        }
    }

    /**
     * Determines if a multifactor sequence should be enforced.
     * <p>
     * Currently, this is a simple check of the user's configuration, but it is
     * designed to be extended for Risk-Based or Policy-Based triggers.
     * </p>
     */
    private boolean isMfaRequired(MfaConfig mfaConfig) {
        return mfaConfig.mfaEnabled();
    }

    /**
     * Constructs a single-step flow for users not requiring MFA.
     */
    private List<AuthStep> getAuthStepsForSingleFactor(MfaConfig mfaConfig) {
        List<AuthMethodType> availableAuthMethods = mfaConfig.enabledMethods()
                .stream()
                .filter(AuthMethodType::isEligibleForSingleFactorLogin)
                .toList();

        return List.of(new AuthStep(availableAuthMethods));
    }

    /**
     * Constructs a mandatory two-step flow (Knowledge then Possession).
     */
    private List<AuthStep> getAuthStepsForMultiFactor(MfaConfig mfaConfig) {
        // Step 1: Something you know (Password/PIN)
        AuthStep knowledgeStep = new AuthStep(
                mfaConfig.enabledMethods()
                        .stream()
                        .filter(AuthMethodType::isEligibleForFirstStepInMfaLogin)
                        .toList()
        );

        // Step 2: Something you have (SMS/Email/TOTP)
        AuthStep mfaStep = new AuthStep(
                mfaConfig.enabledMethods()
                        .stream()
                        .filter(AuthMethodType::isEligibleForMfaStep)
                        .toList()
        );

        return List.of(knowledgeStep, mfaStep);
    }
}