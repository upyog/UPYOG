package org.upyog.dashboard.repository.querybuilder;

/**
 * Query constants class for SQL queries related to ingestion summary
 * persistence. Defines immutable, reusable SQL query strings.
 */
public final class IngestionSummaryQueryBuilder {

    /**
     * Private constructor to prevent instantiation of static query constant
     * holder class.
     */
    private IngestionSummaryQueryBuilder() {
        // Prevent instantiation
    }

    /**
     * Query to retrieve the last successful ingestion date for a specific
     * tenant and module.
     */
    public static final String SELECT_LAST_SUCCESSFUL_DATE_QUERY
            = "SELECT last_successful_date FROM ingestion_module_summary WHERE tenant_id = :tenantId AND module_name = :moduleName";

    /**
     * Query to insert or update the last successful and attempted ingestion
     * dates in ingestion_module_summary.
     */
    public static final String UPSERT_LAST_SUCCESSFUL_DATE_QUERY
            = "INSERT INTO ingestion_module_summary ("
            + "   id, tenant_id, module_name, last_successful_date, last_attempted_date, created_by, last_modified_by"
            + ") VALUES (:id, :tenantId, :moduleName, :lastSuccessfulDate, :lastAttemptedDate, :createdBy, :lastModifiedBy) "
            + "ON CONFLICT (tenant_id, module_name) "
            + "DO UPDATE SET last_successful_date = EXCLUDED.last_successful_date, "
            + "              last_attempted_date = EXCLUDED.last_attempted_date, "
            + "              last_modified_by = EXCLUDED.last_modified_by, "
            + "              last_modified_time = (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::bigint";

    /**
     * Query to insert or update the last attempted ingestion date in
     * ingestion_module_summary.
     */
    public static final String UPSERT_LAST_ATTEMPTED_DATE_QUERY
            = "INSERT INTO ingestion_module_summary ("
            + "   id, tenant_id, module_name, last_successful_date, last_attempted_date, created_by, last_modified_by"
            + ") VALUES (:id, :tenantId, :moduleName, :lastSuccessfulDate, :lastAttemptedDate, :createdBy, :lastModifiedBy) "
            + "ON CONFLICT (tenant_id, module_name) "
            + "DO UPDATE SET last_attempted_date = EXCLUDED.last_attempted_date, "
            + "              last_modified_by = EXCLUDED.last_modified_by, "
            + "              last_modified_time = (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::bigint";

    /**
     * Query to retrieve all distinct dates successfully ingested within a date
     * range across daily and legacy tables.
     */
    public static final String SELECT_SUCCESSFUL_DATES_IN_RANGE_QUERY
            = "SELECT DISTINCT push_date FROM ("
            + "  SELECT push_date FROM ingestion_detail WHERE tenant_id = :tenantId AND module_name = :moduleName AND ingestion_status = 'SUCCESS' AND push_date >= :startDate AND push_date <= :endDate "
            + "  UNION "
            + "  SELECT push_date FROM legacy_data_ingestion_detail WHERE tenant_id = :tenantId AND module_name = :moduleName AND ingestion_status = 'SUCCESS' AND push_date >= :startDate AND push_date <= :endDate "
            + ") combined_dates";

    /**
     * Query to update the module detail table marking legacy data as ingested.
     */
    public static final String UPDATE_MODULE_DETAIL_TABLE_QUERY
            = "UPDATE ingestion_module_detail SET last_ingested_date = :lastIngestedDate, is_legacy_data_ingested = TRUE, last_modified_time = (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::bigint "
            + "WHERE tenant_id = :tenantId AND module_name = :moduleName";

    /**
     * Query to retrieve all distinct push dates registered in
     * legacy_data_ingestion_detail for a tenant and module.
     */
    public static final String SELECT_LEGACY_JOB_DATES_QUERY
            = "SELECT DISTINCT push_date FROM legacy_data_ingestion_detail WHERE tenant_id = :tenantId AND module_name = :moduleName";

    /**
     * Query to insert a new legacy job record into
     * legacy_data_ingestion_detail.
     */
    public static final String INSERT_LEGACY_JOB_QUERY
            = "INSERT INTO legacy_data_ingestion_detail ("
            + "   module_ingestion_id, tenant_id, module_name, push_date, start_date, end_date, ingestion_status, exception_code, created_by, last_modified_by"
            + ") VALUES (:id, :tenantId, :moduleName, :pushDate, :startDate, :endDate, :status, :exceptionCode, :createdBy, :lastModifiedBy)";

    /**
     * Query to select pending or failed legacy jobs ordered by push date
     * ascending up to a specified limit.
     */
    public static final String SELECT_PENDING_OR_FAILED_LEGACY_JOBS_QUERY
            = "SELECT module_ingestion_id, push_date FROM legacy_data_ingestion_detail "
            + "WHERE tenant_id = :tenantId AND module_name = :moduleName AND ingestion_status IN ('NOT_STARTED', 'FAILURE') "
            + "ORDER BY push_date ASC LIMIT :limit";

    /**
     * Query to update the status and payload data of an existing legacy
     * ingestion job.
     */
    public static final String UPDATE_LEGACY_JOB_STATUS_QUERY
            = "UPDATE legacy_data_ingestion_detail "
            + "SET ingestion_status = :status, request_data = :requestData::jsonb, response_data = :responseData::jsonb, last_modified_time = (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::bigint "
            + "WHERE module_ingestion_id = :id";

    /**
     * Query to acquire a row-level pessimistic lock (FOR UPDATE) on
     * ingestion_module_summary for a tenant and module.
     */
    public static final String SELECT_FOR_UPDATE_SUMMARY_QUERY
            = "SELECT id FROM ingestion_module_summary WHERE tenant_id = :tenantId AND module_name = :moduleName FOR UPDATE";

    /**
     * Query to find overlapping successful legacy jobs within a given date
     * window.
     */
    public static final String SELECT_OVERLAPPING_SUCCESSFUL_LEGACY_JOBS_QUERY
            = "SELECT module_ingestion_id, push_date, start_date, end_date "
            + "FROM legacy_data_ingestion_detail "
            + "WHERE tenant_id = :tenantId AND module_name = :moduleName AND ingestion_status = 'SUCCESS' "
            + "  AND ((start_date IS NOT NULL AND end_date IS NOT NULL AND start_date <= :endDate AND end_date >= :startDate) "
            + "       OR (push_date >= :startDate AND push_date <= :endDate))";

    /**
     * Query to insert a daily ingestion detail record into ingestion_detail.
     */
    public static final String INSERT_INGESTION_DETAIL_QUERY
            = "INSERT INTO ingestion_detail ("
            + "   module_ingestion_id, module_detail_id, tenant_id, module_name, push_date, request_data, response_data, ingestion_status, exception_code, created_by, last_modified_by"
            + ") VALUES (:moduleIngestionId, :moduleDetailId, :tenantId, :moduleName, :pushDate, :requestData::jsonb, :responseData::jsonb, :ingestionStatus, :exceptionCode, :createdBy, :lastModifiedBy)";
}
