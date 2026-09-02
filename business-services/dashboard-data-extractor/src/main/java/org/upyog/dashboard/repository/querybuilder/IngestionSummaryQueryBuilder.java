package org.upyog.dashboard.repository.querybuilder;

import org.springframework.stereotype.Component;

/**
 * Query builder class for managing SQL queries related to ingestion summary persistence.
 * Separates SQL query definitions from the main repository logic.
 */
@Component
public class IngestionSummaryQueryBuilder {

    /**
     * Returns a SQL query that selects the {@code last_successful_date} from
     * {@code ingestion_module_summary} for a given tenant and module.
     *
     * @return the parameterised SELECT query string
     */
    public String getSelectLastSuccessfulDateQuery() {
        return "SELECT last_successful_date FROM ingestion_module_summary WHERE tenant_id = :tenantId AND module_name = :moduleName";
    }

    /**
     * Returns an UPSERT query that inserts a new row into {@code ingestion_module_summary}
     * or updates {@code last_successful_date}, {@code last_attempted_date}, and audit fields
     * on conflict with the unique {@code (tenant_id, module_name)} key.
     *
     * @return the parameterised INSERT … ON CONFLICT query string
     */
    public String getUpsertLastSuccessfulDateQuery() {
        return "INSERT INTO ingestion_module_summary (" +
               "   id, tenant_id, module_name, last_successful_date, last_attempted_date, created_by, created_time, last_modified_by, last_modified_time" +
               ") VALUES (:id, :tenantId, :moduleName, :lastSuccessfulDate, :lastAttemptedDate, :createdBy, :createdTime, :lastModifiedBy, :lastModifiedTime) " +
               "ON CONFLICT (tenant_id, module_name) " +
               "DO UPDATE SET last_successful_date = EXCLUDED.last_successful_date, " +
               "              last_attempted_date = EXCLUDED.last_attempted_date, " +
               "              last_modified_by = EXCLUDED.last_modified_by, " +
               "              last_modified_time = EXCLUDED.last_modified_time";
    }

    /**
     * Returns an UPSERT query that inserts a new row into {@code ingestion_module_summary}
     * or updates only {@code last_attempted_date} and audit fields on conflict with the
     * unique {@code (tenant_id, module_name)} key.
     *
     * @return the parameterised INSERT … ON CONFLICT query string
     */
    public String getUpsertLastAttemptedDateQuery() {
        return "INSERT INTO ingestion_module_summary (" +
               "   id, tenant_id, module_name, last_successful_date, last_attempted_date, created_by, created_time, last_modified_by, last_modified_time" +
               ") VALUES (:id, :tenantId, :moduleName, :lastSuccessfulDate, :lastAttemptedDate, :createdBy, :createdTime, :lastModifiedBy, :lastModifiedTime) " +
               "ON CONFLICT (tenant_id, module_name) " +
               "DO UPDATE SET last_attempted_date = EXCLUDED.last_attempted_date, " +
               "              last_modified_by = EXCLUDED.last_modified_by, " +
               "              last_modified_time = EXCLUDED.last_modified_time";
    }

    /**
     * Returns a query that selects distinct {@code push_date} values within a date range
     * that were successfully ingested via either the daily pipeline ({@code ingestion_detail})
     * or the legacy pipeline ({@code legacy_data_ingestion_detail}).
     *
     * <p>Parameters (in order): tenantId, moduleName, startDate, endDate, tenantId, moduleName,
     * startDate, endDate.
     *
     * @return the parameterised UNION SELECT query string
     */
    public String getSelectSuccessfulDatesInRangeQuery() {
        return "SELECT DISTINCT push_date FROM (" +
               "  SELECT push_date FROM ingestion_detail WHERE tenant_id = :tenantId AND module_name = :moduleName AND ingestion_status = 'SUCCESS' AND push_date >= :startDate AND push_date <= :endDate " +
               "  UNION " +
               "  SELECT push_date FROM legacy_data_ingestion_detail WHERE tenant_id = :tenantId AND module_name = :moduleName AND ingestion_status = 'SUCCESS' AND push_date >= :startDate AND push_date <= :endDate " +
               ") combined_dates";
    }

    /**
     * Returns a SQL query that updates the {@code ingestion_module_detail} table, marking
     * the last ingested date and setting the legacy data ingested flag to {@code TRUE}.
     *
     * @return the parameterised UPDATE query string
     */
    public String getUpdateModuleDetailTableQuery() {
        return "UPDATE ingestion_module_detail SET last_ingested_date = :lastIngestedDate, is_legacy_data_ingested = TRUE, last_modified_time = :lastModifiedTime " +
               "WHERE tenant_id = :tenantId AND module_name = :moduleName";
    }

    /**
     * Returns a query that selects all distinct {@code push_date} values that have been
     * registered as legacy jobs for the given tenant and module.
     *
     * @return the parameterised SELECT DISTINCT query string
     */
    public String getSelectLegacyJobDatesQuery() {
        return "SELECT DISTINCT push_date FROM legacy_data_ingestion_detail WHERE tenant_id = :tenantId AND module_name = :moduleName";
    }

    /**
     * Returns a SQL query that inserts a new legacy ingestion job record into
     * {@code legacy_data_ingestion_detail} with an initial status of {@code NOT_STARTED}.
     *
     * @return the parameterised INSERT query string
     */
    public String getInsertLegacyJobQuery() {
        return "INSERT INTO legacy_data_ingestion_detail (" +
               "   module_ingestion_id, tenant_id, module_name, push_date, start_date, end_date, ingestion_status, exception_code, created_by, created_time, last_modified_by, last_modified_time" +
               ") VALUES (:id, :tenantId, :moduleName, :pushDate, :startDate, :endDate, :status, :exceptionCode, :createdBy, :createdTime, :lastModifiedBy, :lastModifiedTime)";
    }

    /**
     * Returns a query that retrieves pending or failed legacy jobs for the given tenant
     * and module, ordered by {@code push_date} ascending, up to the specified row limit.
     *
     * @return the parameterised SELECT query string
     */
    public String getSelectPendingOrFailedLegacyJobsQuery() {
        return "SELECT module_ingestion_id, push_date FROM legacy_data_ingestion_detail " +
               "WHERE tenant_id = :tenantId AND module_name = :moduleName AND ingestion_status IN ('NOT_STARTED', 'FAILURE') " +
               "ORDER BY push_date ASC LIMIT :limit";
    }

    /**
     * Returns a SQL query that updates the status, request payload, and response payload
     * of a specific legacy job in {@code legacy_data_ingestion_detail}.
     *
     * @return the parameterised UPDATE query string
     */
    public String getUpdateLegacyJobStatusQuery() {
        return "UPDATE legacy_data_ingestion_detail " +
               "SET ingestion_status = :status, request_data = :requestData::jsonb, response_data = :responseData::jsonb, last_modified_time = :lastModifiedTime " +
               "WHERE module_ingestion_id = :id";
    }

    /**
     * Returns a SQL query that acquires a row-level lock (FOR UPDATE) on
     * {@code ingestion_module_summary} for a given tenant and module.
     *
     * @return the parameterised SELECT FOR UPDATE query string
     */
    public String getSelectForUpdateSummaryQuery() {
        return "SELECT id FROM ingestion_module_summary WHERE tenant_id = :tenantId AND module_name = :moduleName FOR UPDATE";
    }

    /**
     * Returns a query checking for any successful legacy batch or daily record overlapping the given date range.
     *
     * @return the parameterised SELECT query string
     */
    public String getSelectOverlappingSuccessfulLegacyJobsQuery() {
        return "SELECT module_ingestion_id, push_date, start_date, end_date " +
               "FROM legacy_data_ingestion_detail " +
               "WHERE tenant_id = :tenantId AND module_name = :moduleName AND ingestion_status = 'SUCCESS' " +
               "  AND ((start_date IS NOT NULL AND end_date IS NOT NULL AND start_date <= :endDate AND end_date >= :startDate) " +
               "       OR (push_date >= :startDate AND push_date <= :endDate))";
    }
}
