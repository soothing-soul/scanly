package com.scanly.iam.auth.email;

/**
 * A data transfer record representing the specific requirements for triggering
 * an email OTP generation during an authentication step.
 * <p>
 * While currently empty, this component serves as a type-safe marker within the
 * {@code StepAuthRequest} wrapper. It ensures that the {@link EmailOtpAuthHandler}
 * and its associated executors are processing a "generation" intent rather than
 * a "verification" intent.
 * <p>
 * In future iterations, this payload can be expanded to include specific
 * delivery metadata (e.g., a masked email preference) without breaking the
 * generic signature of the authentication handler pipeline.
 */
public record EmailOtpGenerationPayload() {
}