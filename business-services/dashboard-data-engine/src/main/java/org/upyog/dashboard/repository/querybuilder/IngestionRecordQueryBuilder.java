package org.upyog.dashboard.repository.querybuilder;

import org.upyog.dashboard.common.constants.DashboardConstants;

/**
 * Query constants for persisting ingestion detail records and error logs into database tables.
 */
public final class IngestionRecordQueryBuilder {

    /**
     * Private constructor to prevent instantiation of static query builder constant class.
     */
    private IngestionRecordQueryBuilder() {
        // Prevent instantiation
    }

    public static final String INSERT_INGESTION_DETAIL =
            "INSERT INTO ingestion_detail (" +
            "module_ingestion_id, tenant_id, module_name, push_date, request_data, " +
            "response_data, ingestion_status, exception_code, created_by, created_time, last_modified_by, last_modified_time) " +
            "VALUES (:moduleIngestionId, :tenantId, :moduleName, TO_DATE(:pushDate, '" + DashboardConstants.SQL_DATE_FORMAT + "'), :requestData::jsonb, :responseData::jsonb, :ingestionStatus, :exceptionCode, :createdBy, :createdTime, :lastModifiedBy, :lastModifiedTime)";

    public static final String INSERT_ADAPTER_INGESTION_ERROR_LOG =
            "INSERT INTO adapter_ingestion_error_log (" +
            "id, tenant_id, module_name, error_date, issue_description, created_time, created_by) " +
            "VALUES (:id, :tenantId, :moduleName, :errorDate, :issueDescription, :createdTime, :createdBy)";
}
