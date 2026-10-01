package org.egov.nationaldashboardingest.repository.querybuilder;

import org.springframework.stereotype.Component;

/**
 * Query builder component holding static SQL queries for {@code ug_bulk_ingest_job} database operations.
 * <p>
 * Centralizes SQL query string definitions for bulk ingestion job state checks.
 * </p>
 */
@Component
public class BulkIngestJobQueryBuilder {

    /**
     * Static SQL query to retrieve the most recent bulk ingestion job record matching a given S3 file name.
     */
    public static final String SELECT_JOB_BY_FILE_NAME =
            "SELECT id, file_name AS fileName, state_code AS stateCode, status, " +
            "total_rows_processed AS totalRowsProcessed, failed_rows_count AS failedRowsCount, " +
            "created_time AS createdTime, last_modified_time AS lastModifiedTime " +
            "FROM ug_bulk_ingest_job WHERE file_name = ? ORDER BY created_time DESC LIMIT 1";

    /**
     * Private constructor to prevent direct instantiation of utility class.
     */
    private BulkIngestJobQueryBuilder() {
        // Private constructor enforcing static access
    }
}
