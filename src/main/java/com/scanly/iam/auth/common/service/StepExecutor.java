package com.scanly.iam.auth.common.service;

import com.scanly.iam.auth.common.model.StepContext;

/**
 * Strategy interface for executing a specific step within an authentication flow.
 * <p>
 * Implementations of this interface encapsulate the business logic required to
 * process a specific authentication challenge (e.g., validating a TOTP code,
 * verifying a password, or sending an SMS).
 * </p>
 * <p>
 * By using a generic type {@code <T>}, the executor can define exactly what
 * kind of payload it expects, ensuring type safety across different
 * authentication strategies.
 * </p>
 *
 * <p>
 *     The Goal of this interface is to extract the operations common to all the
 *     steps, like verifying the flowId, or mfaToken etc. in a common service and
 *     delegate the method specific logic through this executor.
 *
 *     This way, a central service will do the pre-requisites for all the steps and
 *     delegate to the executor only when it is required.
 *
 *     If the step fails, the executor is supposed to throw the relevant exception.
 * </p>
 *
 *
 * @param <T> The type of the data payload required to complete this step.
 */
public interface StepExecutor<T> {

    /**
     * Executes the business logic associated with this authentication step.
     * <p>
     * This method typically involves validating the {@code payload} against
     * the system of record by resolving the correct user through {@link StepContext}
     * </p>
     *
     * @param payload     The credential data or action parameters provided by the user.
     * @param stepContext The metadata identifying the user and the specific authentication
     *                    flow session.
     */
    void execute(T payload, StepContext stepContext);
}