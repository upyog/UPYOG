package org.egov.nationaldashboardingest.utils;

/**
 * Centralized constant values for bulk data ingestion operations.
 * <p>
 * Contains integer status codes, string labels, standard response messages, and error codes
 * shared across controller endpoints, service layers, and Kafka consumers.
 * </p>
 */
public final class BulkIngestConstants {

    /**
     * Private constructor to prevent direct instantiation of utility class.
     */
    private BulkIngestConstants() {
        // Private constructor to enforce non-instantiability
    }

    // Status Codes
    /** Status code (1) representing successful validation and Kafka queue submission. */
    public static final int STATUS_CODE_SUCCESS = 1;
    /** Status code (2) indicating the requested file is already queued or currently processing. */
    public static final int STATUS_CODE_ALREADY_PRESENT = 2;
    /** Status code (3) indicating internal failure during validation or Kafka dispatch. */
    public static final int STATUS_CODE_FAILED = 3;

    // Status Text
    /** Status text string representing successful queuing. */
    public static final String STATUS_SUCCESS = "SUCCESS";
    /** Status text string representing duplicate or already present request. */
    public static final String STATUS_ALREADY_PRESENT = "ALREADY_PRESENT";
    /** Status text string representing failed execution. */
    public static final String STATUS_FAILED = "FAILED";

    // Job Execution Status Constants
    /** Job execution status indicating background file processing is actively in progress. */
    public static final String JOB_STATUS_IN_PROGRESS = "IN_PROGRESS";
    /** Job execution status indicating all rows in the bulk file were processed successfully. */
    public static final String JOB_STATUS_COMPLETED = "COMPLETED";
    /** Job execution status indicating an error occurred during file download or row batching. */
    public static final String JOB_STATUS_FAILED = "FAILED";

    // Response Messages
    /** User-facing success response message returned on successful initialization. */
    public static final String MSG_INIT_SUCCESS = "Bulk ingest init request pushed to queue successfully";
    /** User-facing response message returned when a duplicate file is already queued. */
    public static final String MSG_INIT_ALREADY_PRESENT = "File is already present in Kafka queue";
    /** User-facing error message returned when initialization fails. */
    public static final String MSG_INIT_FAILED = "Failed to initialize bulk ingest request";
    /** Error message returned when request structure or mandatory fields are invalid. */
    public static final String MSG_INVALID_INIT_REQUEST = "Invalid Bulk Ingest Init Request";

    // Error Codes
    /** Exception error code for invalid or malformed initialization requests. */
    public static final String ERR_CODE_INVALID_INIT_REQUEST = "EG_NDI_INVALID_INIT_REQUEST";
    /** Exception error code when duplicate file processing is detected. */
    public static final String ERR_CODE_DUPLICATE_FILE = "EG_NDI_DUPLICATE_FILE";
    /** Generic error code indicating resource or entity already exists. */
    public static final String ERR_CODE_ALREADY_EXISTS = "ALREADY_EXISTS";
}
