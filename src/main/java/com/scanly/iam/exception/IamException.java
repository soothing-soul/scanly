package com.scanly.iam.exception;

import com.scanly.common.exception.ScanlyBaseException;
import com.scanly.common.exception.ScanlyError;

/**
 * Base abstract class for all exceptions occurring within the Identity and Access Management (IAM) module.
 * <p>
 * This class serves as a specialized extension of {@link ScanlyBaseException}, ensuring that all
 * IAM-related errors carry a consistent {@link ScanlyError} metadata object.
 * </p>
 * <b>Usage:</b> This class should not be thrown directly. Instead, create specific domain exceptions
 * (e.g., {@code UserNotFoundException}) that extend this class.
 * @version 1.0
 */
public abstract class IamException extends ScanlyBaseException {

  /**
   * Constructs a new IamException with a detailed message and a specific error code.
   * @param message The human-readable error message.
   * @param error   The {@link ScanlyError} containing the internal error code and classification.
   */
  protected IamException(String message, ScanlyError error) {
    super(message, error);
  }

  /**
   * Constructs a new IamException with a detailed message, an error code, and a root cause.
   * @param message The human-readable error message.
   * @param error   The {@link ScanlyError} metadata.
   * @param cause   The underlying cause of the exception (used for exception chaining).
   */
  protected IamException(String message, ScanlyError error, Throwable cause) {
    super(message, error, cause);
  }
}