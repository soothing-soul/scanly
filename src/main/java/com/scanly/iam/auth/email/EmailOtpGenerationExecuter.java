package com.scanly.iam.auth.email;

import com.scanly.iam.auth.common.exception.ConcurrentRequestException;
import com.scanly.iam.auth.common.exception.UserNotFoundException;
import com.scanly.iam.auth.common.model.StepContext;
import com.scanly.iam.auth.common.service.StepExecutor;
import com.scanly.iam.user.domain.User;
import com.scanly.iam.user.service.UserLookupService;
import com.scanly.infra.notification.common.NotificationService;
import com.scanly.infra.notification.email.message.EmailMessage;
import com.scanly.infra.notification.email.message.EmailMessageFactory;
import com.scanly.infra.notification.email.message.otp.OtpEmailInput;
import com.scanly.infra.notification.email.model.EmailRequest;
import com.scanly.otp.model.OtpGenerationContext;
import com.scanly.otp.model.OtpGenerationResult;
import com.scanly.otp.model.OtpGenerationStatus;
import com.scanly.otp.model.OtpPurpose;
import com.scanly.otp.service.OtpService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * Concrete implementation of a {@link StepExecutor} responsible for the
 * generation and dispatch phase of an Email OTP authentication step.
 * <p>
 * This services ensures that a secure, time-bound challenge is created and delivered
 * to the user's registered email address by coordinating between infrastructure layers
 * {@link OtpService}, {@link NotificationService}, and domain layers {@link UserLookupService}.
 * </p>
 */
@Service
public class EmailOtpGenerationExecuter implements StepExecutor<EmailOtpGenerationPayload> {

    private final OtpService otpService;
    private final NotificationService notificationService;
    private final UserLookupService userLookupService;
    private final EmailMessageFactory emailMessageFactory;

    /**
     * Constructs the executor with required domain and infrastructure services.
     */
    public EmailOtpGenerationExecuter(
            OtpService otpService,
            NotificationService notificationService,
            UserLookupService userLookupService,
            EmailMessageFactory emailMessageFactory
    ) {
        this.otpService = otpService;
        this.notificationService = notificationService;
        this.userLookupService = userLookupService;
        this.emailMessageFactory = emailMessageFactory;
    }

    /**
     * Orchestrates the OTP generation and email dispatch process.
     * <p>
     * This method acts as the transactional unit for the "Send OTP" step.
     * It correlates the {@code flowId} from the {@link StepContext} with
     * the OTP challenge to ensure the verification phase remains consistent.
     * </p>
     *
     * @param payload     The (currently empty) generation payload marker.
     * @param stepContext The context of the current authentication flow,
     * containing the user ID and flow identifier.
     */
    @Override
    public void execute(EmailOtpGenerationPayload payload, StepContext stepContext) {
        String otp = generateOtp(stepContext.flowId());
        EmailRequest emailRequest = generateEmailRequest(otp, stepContext.userId());

        notificationService.send(emailRequest);
    }

    /**
     * Requests the centralized {@link OtpService} to create a new challenge.
     * <p>
     * For email authentication, we enforce a strict policy:
     * <ul>
     * <li>Purpose: {@code AUTHENTICATION}</li>
     * <li>Max Attempts: 3</li>
     * <li>TTL: 5 Minutes</li>
     * </ul>
     * </p>
     *
     * @param flowId The identifier used as the challengeId in the OTP module.
     * @return The raw OTP string to be included in the email.
     */
    private String generateOtp(String flowId) {
        OtpGenerationResult otpGenerationResult = otpService.generate(
                new OtpGenerationContext(
                        flowId,
                        OtpPurpose.AUTHENTICATION,
                        3,
                        Duration.ofMinutes(5)
                )
        );

        if (otpGenerationResult.status() == OtpGenerationStatus.CONCURRENT_REQUEST) {
            throw new ConcurrentRequestException(
                    String.format("Otp generation failed, flowId: %s", flowId)
            );
        }

        return otpGenerationResult.otp();
    }

    /**
     * Constructs a specialized {@link EmailRequest} for OTP delivery.
     * <p>
     * This method resolves the user's email address and utilizes the
     * {@link EmailMessageFactory} to generate the email content for the
     * notification.
     * </p>
     *
     * @param otp    The generated raw OTP.
     * @param userId The ID of the user receiving the code.
     * @return A fully prepared {@code EmailRequest} ready for dispatch.
     */
    public EmailRequest generateEmailRequest(String otp, UUID userId) {
        String emailAddress = findEmailAddress(userId);
        EmailMessage emailMessage = emailMessageFactory.create(
                new OtpEmailInput(
                        otp,
                        Duration.ofMinutes(5),
                        com.scanly.infra.notification.email.message.otp.OtpPurpose.AUTHENTICATION
                )
        );

        return EmailRequest.from(emailAddress, emailMessage);
    }

    /**
     * Retrieves the user's email address from the domain layer.
     *
     * @param userId The unique identifier of the user.
     * @return The registered email address.
     * @throws UserNotFoundException if the provided UUID does not match an existing user.
     */
    private String findEmailAddress(UUID userId) {
        User user = userLookupService.findById(userId).orElse(null);

        if (user == null) {
            throw new UserNotFoundException(
                    String.format("User with id %s not found", userId)
            );
        }

        return user.email();
    }
}