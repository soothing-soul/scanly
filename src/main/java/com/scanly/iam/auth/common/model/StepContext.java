package com.scanly.iam.auth.common.model;

import com.scanly.iam.auth.common.service.AuthHandler;
import com.scanly.iam.auth.common.service.StepExecutor;

import java.util.UUID;

/**
 * An internal context object that bridges a user's identity with their current
 * authentication session.
 * <p>
 * This record is typically passed on by the centralized {@link AuthHandler} to
 * individual implementation of {@link StepExecutor} to ensure that the executors
 * can resolve the correct user and session based on this context.
 * </p>
 *
 * @param userId The unique internal identifier of the user attempting to authenticate.
 * @param flowId The transient identifier for the current multistep authentication
 *               lifecycle (MFA flow).
 */
public record StepContext(
        UUID userId,
        String flowId
) {}