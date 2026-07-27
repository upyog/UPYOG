package org.upyog.adapter.repository.querybuilder;

import org.springframework.stereotype.Component;

/**
 * Query builder class for managing SQL queries related to ingestion summary persistence.
 * Separates SQL query definitions from the main repository logic.
 */
@Component
public class IngestionSummaryQueryBuilder {

    public String getSelectLastSuccessfulDateQuery() {
        return "SELECT last_successful_date FROM ingestion_module_summary WHERE tenant_id = ? AND module_name = ?";
    }

    public String getUpsertLastSuccessfulDateQuery() {
        return "INSERT INTO ingestion_module_summary (" +
               "   id, tenant_id, module_name, last_successful_date, last_attempted_date, created_by, created_time, last_modified_by, last_modified_time" +
               ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
               "ON CONFLICT (tenant_id, module_name) " +
               "DO UPDATE SET last_successful_date = EXCLUDED.last_successful_date, " +
               "              last_attempted_date = EXCLUDED.last_attempted_date, " +
               "              last_modified_by = EXCLUDED.last_modified_by, " +
               "              last_modified_time = EXCLUDED.last_modified_time";
    }

    public String getUpsertLastAttemptedDateQuery() {
        return "INSERT INTO ingestion_module_summary (" +
               "   id, tenant_id, module_name, last_successful_date, last_attempted_date, created_by, created_time, last_modified_by, last_modified_time" +
               ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
               "ON CONFLICT (tenant_id, module_name) " +
               "DO UPDATE SET last_attempted_date = EXCLUDED.last_attempted_date, " +
               "              last_modified_by = EXCLUDED.last_modified_by, " +
               "              last_modified_time = EXCLUDED.last_modified_time";
    }

    public String getSelectSuccessfulDatesInRangeQuery() {
        return "SELECT DISTINCT push_date FROM (" +
               "  SELECT push_date FROM ingestion_detail WHERE tenant_id = ? AND module_name = ? AND ingestion_status = 'SUCCESS' AND push_date >= ? AND push_date <= ? " +
               "  UNION " +
               "  SELECT push_date FROM legacy_data_ingestion_detail WHERE tenant_id = ? AND module_name = ? AND ingestion_status = 'SUCCESS' AND push_date >= ? AND push_date <= ? " +
               ") combined_dates";
    }

    public String getUpdateModuleDetailTableQuery() {
        return "UPDATE ingestion_module_detail SET last_ingested_date = ?, is_legacy_data_ingested = TRUE, last_modified_time = ? " +
               "WHERE tenant_id = ? AND module_name = ?";
    }

    public String getSelectLegacyJobDatesQuery() {
        return "SELECT DISTINCT push_date FROM legacy_data_ingestion_detail WHERE tenant_id = ? AND module_name = ?";
    }

    public String getInsertLegacyJobQuery() {
        return "INSERT INTO legacy_data_ingestion_detail (" +
               "   module_ingestion_id, tenant_id, module_name, push_date, ingestion_status, exception_code, created_by, created_time, last_modified_by, last_modified_time" +
               ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    public String getSelectPendingOrFailedLegacyJobsQuery() {
        return "SELECT module_ingestion_id, push_date FROM legacy_data_ingestion_detail " +
               "WHERE tenant_id = ? AND module_name = ? AND ingestion_status IN ('NOT_STARTED', 'FAILURE') " +
               "ORDER BY push_date ASC LIMIT ?";
    }

    public String getUpdateLegacyJobStatusQuery() {
        return "UPDATE legacy_data_ingestion_detail " +
               "SET ingestion_status = ?, request_data = ?::jsonb, response_data = ?::jsonb, last_modified_time = ? " +
               "WHERE module_ingestion_id = ?";
    }
}
