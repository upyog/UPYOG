package org.egov.nationaldashboardingest.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Domain and entity model representing a bulk file ingestion job execution.
 * <p>
 * Tracks file-level processing status, row execution statistics, and timestamps
 * in the {@code ug_bulk_ingest_job} table to ensure single-execution
 * idempotency.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkIngestJob {

    /**
     * Unique job identifier (e.g. UUID or SHA-256 hash derived from S3 file
     * name).
     */
    private String id;

    /**
     * S3 object key or file name identifier.
     */
    private String fileName;

    /**
     * Target state code or tenant identifier.
     */
    private String stateCode;

    /**
     * Current execution status (e.g. IN_PROGRESS, COMPLETED, FAILED).
     */
    private String status;

    /**
     * Aggregate total count of rows successfully read and processed.
     */
    @Builder.Default
    private int totalRowsProcessed = 0;

    /**
     * Aggregate count of rows that encountered errors during ingestion.
     */
    @Builder.Default
    private int failedRowsCount = 0;

    /**
     * Epoch timestamp (milliseconds) when the job record was created.
     */
    private Long createdTime;

    /**
     * Epoch timestamp (milliseconds) when the job record was last updated.
     */
    private Long lastModifiedTime;
}
