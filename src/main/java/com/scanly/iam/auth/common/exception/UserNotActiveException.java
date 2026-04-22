package com.scanly.iam.auth.common.exception;

/**
 * Exception thrown when an authentication attempt is made for a user account that
 * is not in an "Active" state.
 * <p>
 * This exception indicates that while the user may have provided valid credentials,
 * administrative or system-level constraints prevent them from logging in.
 * Common scenarios include:
 * <ul>
 * <li>The account has been manually deactivated by an administrator.</li>
 * <li>The account is pending email or phone number verification.</li>
 * <li>The account has been temporarily locked due to security concerns or
 * repeated failed attempts.</li>
 * <li>The account is in a "Suspended" state due to billing or policy violations.</li>
 * </ul>
 * </p>
 * <p>
 * <b>Security Note:</b> Unlike {@link InvalidCredentialsException}, this exception
 * confirms the existence of the user. It should only be thrown <i>after</i> the user
 * has been successfully identified.
 * </p>
 *
 * @see AuthError#USER_NOT_ACTIVE
 */
public class UserNotActiveException extends AuthException {

    /** The constant error mapping that binds this exception to a 403 Forbidden status. */
    private static final AuthError error = AuthError.USER_NOT_ACTIVE;

    /**
     * Constructs a new UserNotActiveException with a custom explanation.
     *
     * @param message A detail message explaining the specific status of the user
     * (e.g., "Account suspended for non-payment").
     */
    public UserNotActiveException(String message) {
        super(message, error);
    }
}