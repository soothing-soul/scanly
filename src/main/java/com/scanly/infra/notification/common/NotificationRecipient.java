package com.scanly.infra.notification.common;

/**
 * A marker interface representing an entity capable of receiving notifications.
 * <p>
 * Any domain object (e.g., User, Admin, ExternalClient) that needs to be
 * a target for notifications must implement this interface. It allows the
 * notification engine to handle diverse entities through a unified type.
 * </p>
 */
public interface NotificationRecipient {}