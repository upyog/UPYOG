package org.egov.nationaldashboardingest.web.models;

import lombok.Getter;
import org.egov.nationaldashboardingest.utils.BulkIngestConstants;
import org.springframework.http.HttpStatus;

/**
 * Enumeration mapping bulk data ingestion outcome codes to user-facing labels, response messages, and HTTP status codes.
 * <p>
 * Centralizes the translation between service layer integer results and HTTP API responses.
 * </p>
 */
@Getter
public enum BulkIngestStatus {

    /** Successful initialization and queueing of the bulk ingestion job (HTTP 200 OK). */
    SUCCESS(BulkIngestConstants.STATUS_CODE_SUCCESS, BulkIngestConstants.STATUS_SUCCESS, BulkIngestConstants.MSG_INIT_SUCCESS, HttpStatus.OK),
    /** Ingestion job for the given file is already present or currently processing (HTTP 200 OK). */
    ALREADY_PRESENT(BulkIngestConstants.STATUS_CODE_ALREADY_PRESENT, BulkIngestConstants.STATUS_ALREADY_PRESENT, BulkIngestConstants.MSG_INIT_ALREADY_PRESENT, HttpStatus.OK),
    /** Ingestion initialization failed due to internal or messaging errors (HTTP 500 Internal Server Error). */
    FAILED(BulkIngestConstants.STATUS_CODE_FAILED, BulkIngestConstants.STATUS_FAILED, BulkIngestConstants.MSG_INIT_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);

    /** Numeric identifier of the status. */
    private final int code;
    /** Status string label. */
    private final String status;
    /** User-facing explanation message. */
    private final String message;
    /** Associated HTTP status code for REST response dispatching. */
    private final HttpStatus httpStatus;

    /**
     * Constructs a {@link BulkIngestStatus} enum instance.
     *
     * @param code       numeric status code
     * @param status     status string label
     * @param message    user-facing explanation message
     * @param httpStatus Spring {@link HttpStatus} representation
     */
    BulkIngestStatus(int code, String status, String message, HttpStatus httpStatus) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    /**
     * Resolves an integer status code to its corresponding {@link BulkIngestStatus} enum instance.
     *
     * @param code integer status code (1 = SUCCESS, 2 = ALREADY_PRESENT, 3 = FAILED)
     * @return matching {@link BulkIngestStatus} enum, or {@link #FAILED} if no direct match is found
     */
    public static BulkIngestStatus fromCode(int code) {
        for (BulkIngestStatus s : values()) {
            if (s.getCode() == code) {
                return s;
            }
        }
        return FAILED;
    }
}
