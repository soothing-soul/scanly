package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when a One-Time Password verification fails and the
 * maximum number of permitted attempts for that challenge has been reached.
 * <p>
 * Unlike {@link OtpInvalidException}, this represents a "hard failure."
 * Once this exception is raised, the associated OTP challenge is considered
 * exhausted and is purged from the system. Any further attempts with the
 * same {@code flowId} will likely result in an {@link OtpNotFoundException}
 * unless a new challenge is generated.
 * <p>
 * This exception maps to {@link AuthError#INVALID_OTP_ATTEMPT_LIMIT_REACHED}.
 * Security layers can use this specific error to trigger secondary actions,
 * such as temporary account lockouts or cooling-off periods to mitigate
 * brute-force attacks.
 */
public class OtpInvalidMaximumLimitReachedException extends AuthException {

  /**
   * The specific domain error indicating that the retry threshold has been breached.
   */
  private static final AuthError error = AuthError.INVALID_OTP_ATTEMPT_LIMIT_REACHED;

  /**
   * Constructs a new exception with a descriptive message.
   *
   * @param message The detailed message explaining that the limit has been reached.
   */
  public OtpInvalidMaximumLimitReachedException(String message) {
    super(message, error);
  }
}