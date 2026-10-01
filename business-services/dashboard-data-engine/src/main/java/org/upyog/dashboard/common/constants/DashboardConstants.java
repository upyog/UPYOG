package org.upyog.dashboard.common.constants;

/**
 * Shared constants across dashboard microservices, defining database query parameter keys,
 * upload and ingestion modes, system identifiers, operation statuses, and formatting patterns.
 * <p>
 * Centralizes static literals to prevent magic strings and ensure contract consistency across
 * data-engine, data-extractor, and downstream ingestion modules.
 * </p>
 */
public class DashboardConstants {

    /**
     * Protected constructor to prevent direct instantiation of utility/constant class.
     */
    protected DashboardConstants() {}

    // DB Parameter Keys
    /** Parameter name for tenant identifier in JDBC named parameter queries. */
    public static final String PARAM_TENANT_ID = "tenantId";
    /** Parameter name for module name in JDBC named parameter queries. */
    public static final String PARAM_MODULE_NAME = "moduleName";
    /** Parameter name for record unique ID in queries. */
    public static final String PARAM_ID = "id";
    /** Parameter name for query start date range. */
    public static final String PARAM_START_DATE = "startDate";
    /** Parameter name for query end date range. */
    public static final String PARAM_END_DATE = "endDate";
    /** Parameter name for job or ingestion status. */
    public static final String PARAM_STATUS = "status";
    /** Parameter name for audit created-by user identifier. */
    public static final String PARAM_CREATED_BY = "createdBy";
    /** Parameter name for audit created timestamp. */
    public static final String PARAM_CREATED_TIME = "createdTime";
    /** Parameter name for audit last-modified-by user identifier. */
    public static final String PARAM_LAST_MODIFIED_BY = "lastModifiedBy";
    /** Parameter name for audit last modified timestamp. */
    public static final String PARAM_LAST_MODIFIED_TIME = "lastModifiedTime";
    /** Parameter name for tracking the last successfully ingested date. */
    public static final String PARAM_LAST_SUCCESSFUL_DATE = "lastSuccessfulDate";
    /** Parameter name for tracking the last attempted ingestion date. */
    public static final String PARAM_LAST_ATTEMPTED_DATE = "lastAttemptedDate";
    /** Parameter name for storing outbound request payload JSON. */
    public static final String PARAM_REQUEST_DATA = "requestData";
    /** Parameter name for storing inbound response payload or error string. */
    public static final String PARAM_RESPONSE_DATA = "responseData";
    /** Parameter name for SQL query result set limit. */
    public static final String PARAM_LIMIT = "limit";
    
    // Engine specific DB Parameter Keys
    /** Parameter name for module ingestion audit record foreign key. */
    public static final String PARAM_MODULE_INGESTION_ID = "moduleIngestionId";
    /** Parameter name for module configuration detail record ID. */
    public static final String PARAM_MODULE_DETAIL_ID = "moduleDetailId";
    /** Parameter name for scheduler execution tracking ID. */
    public static final String PARAM_SCHEDULER_ID = "schedulerId";
    /** Parameter name for dataset push timestamp. */
    public static final String PARAM_PUSH_DATE = "pushDate";
    /** Parameter name for granular ingestion status string. */
    public static final String PARAM_INGESTION_STATUS = "ingestionStatus";
    /** Parameter name for error exception code. */
    public static final String PARAM_EXCEPTION_CODE = "exceptionCode";
    /** Parameter name for error occurrence date. */
    public static final String PARAM_ERROR_DATE = "errorDate";
    /** Parameter name for detailed issue or error stack description. */
    public static final String PARAM_ISSUE_DESCRIPTION = "issueDescription";

    // Common Strings
    /** Standard system user identifier for automated cron jobs and background operations. */
    public static final String SYSTEM_USER = "SYSTEM";
    /** Default API identifier used in UPYOG RequestInfo headers. */
    public static final String API_ID_RAINMAKER = "Rainmaker";
    /** Default locale and language suffix for UPYOG RequestInfo message IDs. */
    public static final String LOCALE_EN_IN_SUFFIX = "|en_IN";

    // Upload Modes
    /** Upload mode designating direct upload to Amazon Simple Storage Service (S3). */
    public static final String UPLOAD_MODE_S3 = "S3";
    /** Upload mode designating upload to eGov Filestore service. */
    public static final String UPLOAD_MODE_FILESTORE = "FILESTORE";
    /** Upload mode designating direct HTTP multipart REST API ingestion. */
    public static final String UPLOAD_MODE_API = "API";
    
    // Statuses
    /** Initial status indicating an ingestion job or batch has not yet started. */
    public static final String STATUS_NOT_STARTED = "NOT_STARTED";
    /** Status indicating successful completion of ingestion or processing. */
    public static final String STATUS_SUCCESS = "SUCCESS";
    /** Status indicating failure of ingestion, upload, or validation. */
    public static final String STATUS_FAILURE = "FAILURE";

    // Ingestion Types / Excel Generation Modes
    /** Ingestion type identifier for recurring daily incremental data extraction. */
    public static final String DAILY = "daily";
    /** Ingestion type identifier for historical legacy backfill data extraction. */
    public static final String LEGACY = "legacy";
    /** Constant alias for daily ingestion type. */
    public static final String INGESTION_TYPE_DAILY = DAILY;
    /** Constant alias for legacy ingestion type. */
    public static final String INGESTION_TYPE_LEGACY = LEGACY;

    // Date Format Patterns
    /** Standard Java date format pattern used across dashboard payloads ({@code dd-MM-yyyy}). */
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    /** Standard PostgreSQL/SQL date format pattern used in database query strings ({@code DD-MM-YYYY}). */
    public static final String SQL_DATE_FORMAT = "DD-MM-YYYY";

    // Excel Generation Limits
    /**
     * Maximum character count allowed in a single Apache POI / Excel workbook cell (32,767 limit minus margin).
     */
    public static final int EXCEL_MAX_CELL_CHAR_LIMIT = 32765;
}
