package org.upyog.dashboard.common.constants;

public class DashboardConstants {
    protected DashboardConstants() {} // Prevent instantiation

    // DB Parameter Keys
    public static final String PARAM_TENANT_ID = "tenantId";
    public static final String PARAM_MODULE_NAME = "moduleName";
    public static final String PARAM_ID = "id";
    public static final String PARAM_START_DATE = "startDate";
    public static final String PARAM_END_DATE = "endDate";
    public static final String PARAM_STATUS = "status";
    public static final String PARAM_CREATED_BY = "createdBy";
    public static final String PARAM_CREATED_TIME = "createdTime";
    public static final String PARAM_LAST_MODIFIED_BY = "lastModifiedBy";
    public static final String PARAM_LAST_MODIFIED_TIME = "lastModifiedTime";
    public static final String PARAM_LAST_SUCCESSFUL_DATE = "lastSuccessfulDate";
    public static final String PARAM_LAST_ATTEMPTED_DATE = "lastAttemptedDate";
    public static final String PARAM_REQUEST_DATA = "requestData";
    public static final String PARAM_RESPONSE_DATA = "responseData";
    public static final String PARAM_LIMIT = "limit";
    
    // Engine specific DB Parameter Keys
    public static final String PARAM_MODULE_INGESTION_ID = "moduleIngestionId";
    public static final String PARAM_PUSH_DATE = "pushDate";
    public static final String PARAM_INGESTION_STATUS = "ingestionStatus";
    public static final String PARAM_EXCEPTION_CODE = "exceptionCode";
    public static final String PARAM_ERROR_DATE = "errorDate";
    public static final String PARAM_ISSUE_DESCRIPTION = "issueDescription";

    // Common Strings
    public static final String SYSTEM_USER = "SYSTEM";
    
    // Statuses
    public static final String STATUS_NOT_STARTED = "NOT_STARTED";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILURE = "FAILURE";

    // Date Format Patterns
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String SQL_DATE_FORMAT = "DD-MM-YYYY";
    
}
