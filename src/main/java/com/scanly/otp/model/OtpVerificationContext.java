package com.scanly.otp.model;

/**
 * A data transfer record that encapsulates the credentials required to attempt
 * an OTP verification.
 * <p>
 * This record is the primary input for the verification logic. It bridges the gap
 * between the user's input (the raw OTP) and the system's tracking mechanism
 * (the {@code challengeId}).
 * <p>
 * By using this context, the OTP service can remain stateless regarding the
 * transport layer, as it only requires these two identifiers to locate the
 * associated {@code OtpState} and perform the validation.
 *
 * @param challengeId The unique identifier used to retrieve the current state
 *                    of the challenge.
 * @param otp         The raw, unhashed character sequence provided by the user
 *                    for verification.
 */
public record OtpVerificationContext(
        String challengeId,
        String otp
) {
}