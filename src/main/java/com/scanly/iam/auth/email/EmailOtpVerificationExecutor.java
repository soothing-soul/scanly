package com.scanly.iam.auth.email;

import com.scanly.iam.auth.common.exception.*;
import com.scanly.iam.auth.common.model.StepContext;
import com.scanly.iam.auth.common.service.StepExecutor;
import com.scanly.otp.model.OtpVerificationContext;
import com.scanly.otp.model.OtpVerificationStatus;
import com.scanly.otp.service.OtpService;
import org.springframework.stereotype.Service;

/**
 * Concrete implementation of a {@link StepExecutor} responsible for validating
 * an email-based OTP during the authentication process.
 * <p>
 * This service acts as the bridge between the high-level IAM authentication flow
 * and the low-level {@link OtpService}. It maps verification outcomes
 * to domain-specific exceptions.
 * </p>
 */
@Service
public class EmailOtpVerificationExecutor implements StepExecutor<EmailOtpVerificationPayload> {

    private final OtpService otpService;

    public EmailOtpVerificationExecutor(OtpService otpService) {
        this.otpService = otpService;
    }

    /**
     * Executes the verification phase by delegating to the OtpService.
     * <p>
     * If the verification is unsuccessful, it delegates the exception
     * throwing logic to {@link #handleVerificationFailure}.
     * </p>
     */
    @Override
    public void execute(EmailOtpVerificationPayload payload, StepContext stepContext) {
        OtpVerificationStatus status = otpService.verify(
                new OtpVerificationContext(
                        stepContext.flowId(),
                        payload.otp()
                )
        );

        if (status != OtpVerificationStatus.SUCCESS) {
            handleVerificationFailure(status, stepContext.flowId());
        }
    }

    /**
     * Translates {@link OtpVerificationStatus} codes into IAM domain-specific
     * runtime exceptions.
     *
     * @param status  The failure status returned by the OTP service.
     * @param flowId  The identifier for the current authentication flow,
     * used for error logging and context.
     *
     * @throws ConcurrentRequestException if a lock cannot be acquired.
     * @throws OtpNotFoundException if the challenge does not exist.
     * @throws OtpExpiredException if the challenge is expired.
     * @throws OtpInvalidException if the code is wrong but there are left attempts.
     * @throws OtpInvalidMaximumLimitReachedException if the code is wrong and
     * no attempts remain.
     */
    private void handleVerificationFailure(OtpVerificationStatus status, String flowId) {
        switch (status) {
            case CONCURRENT_REQUEST ->
                    throw new ConcurrentRequestException(
                            String.format("Concurrent request for OTP challenge: %s", flowId)
                    );

            case NOT_FOUND ->
                    throw new OtpNotFoundException(
                            String.format("OTP not found for flowId: %s", flowId)
                    );

            case EXPIRED ->
                    throw new OtpExpiredException(
                            String.format("OTP expired for flowId: %s", flowId)
                    );

            case INVALID_OTP ->
                    throw new OtpInvalidException(
                            String.format("Invalid OTP for flowId: %s", flowId)
                    );

            case INVALID_OTP_MAXIMUM_ATTEMPT_REACHED ->
                    throw new OtpInvalidMaximumLimitReachedException(
                            String.format("Invalid OTP threshold reached for flowId: %s", flowId)
                    );

            default ->
                    throw new IllegalStateException("Unexpected OTP verification status: " + status);
        }
    }
}