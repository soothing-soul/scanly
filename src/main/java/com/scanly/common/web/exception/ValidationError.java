package com.scanly.common.web.exception;

/**
 * Represents a specific validation failure detected during request processing.
 * <p>
 * This record supports field validation errors where two piece of information
 * must be shared with the UI.
 * <ul>
 * <li><b>Field Name:</b> Name of the field for which the validation failed</li>
 * <li><b>Error Code:</b> Machine readable error code which can be processed
 * by the UI. </li>
 * </ul>
 * </p>
 *
 * @see HttpErrorResponse
 */
public record ValidationError (
        /** The name of the request field that failed validation */
        String field,

        /** A machine-readable string identifying the specific rule violated (e.g., "MIN_LENGTH"). */
        String errorCode
) {}