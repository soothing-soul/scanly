package com.scanly.iam.auth.common.service;

import com.scanly.iam.auth.common.dto.request.StepAuthRequest;
import com.scanly.iam.auth.common.dto.response.AuthResponse;
import com.scanly.iam.auth.common.exception.ConcurrentRequestException;
import com.scanly.iam.auth.common.model.StepContext;
import com.scanly.iam.auth.flow.AuthFlowService;
import com.scanly.infra.cache.redis.RedisLock;
import org.springframework.stereotype.Component;

/**
 * The central orchestrator for processing individual steps within an authentication flow.
 *
 * <p>
 * This component provides a thread-safe execution environment for authentication logic.
 * It ensures that for any given user, only one authentication step is processed at
 * a time, effectively preventing race conditions and state corruption in multistep
 * MFA sequences.
 * </p>
 *
 * <p>
 *     {@code AuthHandler}, executes the same three operations for each of the authentication
 *     step by coordinating with {@link AuthFlowService} and {@link StepExecutor}.
 *
 *     <ul>
 *         <li>
 *             {@code Authorize}: Checks if the step satisfies the prerequisites. Delegates
 *             the responsibility to {@link AuthFlowService}.
 *         </li>
 *         <li>
 *             {@code Execute}: Delegates the actual business logic pertaining to the
 *             authentication to the specific executor
 *         </li>
 *         <li>
 *             {@code Advance}: Transitions to the next state of the authentication flow
 *             and returns the valid {@link AuthResponse}. Delegates the responsibility
 *             to {@link AuthFlowService}
 *         </li>
 *     </ul>
 *
 * </p>
 */
@Component
public class AuthHandler {
    private final AuthLockCache authLockCache;
    private final AuthFlowService authFlowService;

    /**
     * Constructs the AuthHandler with necessary security and flow management services.
     *
     * @param authLockCache    The cache used for distributed locking.
     * @param authFlowService The service responsible for authorizing and advancing auth states.
     */
    public AuthHandler(AuthLockCache authLockCache, AuthFlowService authFlowService) {
        this.authLockCache = authLockCache;
        this.authFlowService = authFlowService;
    }

    /**
     * Executes an authentication step with mandatory distributed locking.
     * <p>
     * The process follows these stages:
     * <ol>
     * <li><b>Authorize:</b> Validates the request and retrieves the session {@link StepContext}.</li>
     * <li><b>Synchronize:</b> Acquires a distributed lock for the specific user ID.</li>
     * <li><b>Execute:</b> Invokes the specific {@link StepExecutor} strategy.</li>
     * <li><b>Advance:</b> Updates the internal flow state and returns the next response.</li>
     * <li><b>Release:</b> Ensures the lock is released regardless of success or failure.</li>
     * </ol>
     * </p>
     *
     * @param <T>             The type of the request payload.
     * @param stepAuthRequest The incoming request containing tokens and credentials.
     * @param stepExecutor    The strategy implementation for the specific auth method.
     * @return An {@link AuthResponse} representing the new state of the authentication flow.
     * @throws ConcurrentRequestException if another request for the user is already in progress.
     */
    public <T> AuthResponse handle(StepAuthRequest<T> stepAuthRequest, StepExecutor<T> stepExecutor) {
        // 1. Validate the context before attempting to lock
        StepContext stepContext = authFlowService.authorize(stepAuthRequest);

        RedisLock lock = null;
        try {
            // 2. Acquire lock to prevent race conditions during state transition
            lock = authLockCache.acquireLock(stepContext.userId());
            if (lock == null) {
                throw new ConcurrentRequestException(
                        String.format(
                                "Concurrent Requests received for userId: %s",
                                stepContext.userId()
                        )
                );
            }

            // 3. Perform the actual credential/action logic
            stepExecutor.execute(stepAuthRequest.payload(), stepContext);

            // 4. Move the state machine forward
            return authFlowService.advance(stepAuthRequest);

        } finally {
            // 5. Always release the lock to avoid deadlocking the user's account
            if (lock != null) {
                authLockCache.releaseLock(lock);
            }
        }
    }
}