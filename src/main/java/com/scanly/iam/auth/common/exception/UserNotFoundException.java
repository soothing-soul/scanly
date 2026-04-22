package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when a requested user identity cannot be located within the
 * identity provider or local database.
 * <p>
 * This exception is typically used during the initial stage of authentication or
 * during administrative tasks (like password resets).
 * </p>
 *
 * @see AuthError#USER_NOT_FOUND
 */
public class UserNotFoundException extends AuthException {

  /** The constant error mapping that binds this exception to a 404 Not Found status. */
  private static final AuthError error = AuthError.USER_NOT_FOUND;

  /**
   * Constructs a new UserNotFoundException with a custom explanation.
   *
   * @param message A detail message explaining which identifier failed to resolve.
   */
  public UserNotFoundException(String message) {
    super(message, error);
  }
}