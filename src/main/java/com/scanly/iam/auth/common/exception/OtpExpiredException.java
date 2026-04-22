package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when a user attempts to verify a One-Time Password that has
 * surpassed its validity period.
 * <p>
 * This exception is typically raised when the underlying OTP service indicates
 * an {@code EXPIRED} status. While the OTP service handles the state cleanup
 * (deletion from cache), this domain exception allows the API to communicate
 * the specific failure reason to the client, usually resulting in a prompt for
 * the user to request a new code.
 * <p>
 * Maps to {@link AuthError#OTP_EXPIRED}.
 */
public class OtpExpiredException extends AuthException {

  /**
   * The specific domain error associated with an expired OTP challenge.
   */
  private static final AuthError error = AuthError.OTP_EXPIRED;

  /**
   * Constructs a new exception with a descriptive message.
   *
   * @param message The detail message (e.g., including the flowId).
   */
  public OtpExpiredException(String message) {
    super(message, error);
  }
}