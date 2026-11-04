package org.upyog.dashboard.constants;

import org.upyog.dashboard.common.constants.DashboardConstants;

/**
 * Constants specific to the data extractor service, extending common dashboard constants.
 */
public final class DashboardExtractorConstants extends DashboardConstants {
    
    /**
     * Private constructor to prevent instantiation of constant class.
     */
    private DashboardExtractorConstants() {}
    
    // Extractor-Specific Hierarchy / JSON Keys
    /** Hierarchy key for Ward level. */
    public static final String KEY_WARD = "ward";
    /** Hierarchy key for ULB (Urban Local Body) level. */
    public static final String KEY_ULB = "ulb";
    /** Hierarchy key for Region level. */
    public static final String KEY_REGION = "region";
    /** Hierarchy key for State level. */
    public static final String KEY_STATE = "state";

    // Query Parameter Keys
    /** Named parameter key for start timestamp in SQL extraction queries. */
    public static final String PARAM_START_TIME = "startTime";
    /** Named parameter key for end timestamp in SQL extraction queries. */
    public static final String PARAM_END_TIME = "endTime";
    /** Named parameter key for module detail ID in ingestion detail queries. */
    public static final String PARAM_MODULE_DETAIL_ID = "moduleDetailId";
    /** Named parameter key for scheduler ID in ingestion detail queries. */
    public static final String PARAM_SCHEDULER_ID = "schedulerId";

    /** Audit user identifier for MDMS tenant synchronization events. */
    public static final String MDMS_SYNC_USER = "MDMS_SYNC";

    // MDMS Client Constants
    /** MDMS module name for tenant master data. */
    public static final String MDMS_MODULE_TENANT = "tenant";
    /** MDMS master name for tenant info. */
    public static final String MDMS_MASTER_TENANTS = "nationalInfo";
    /** Filter pattern for filtering MDMS nationalInfo by state code. */
    public static final String MDMS_NATIONAL_INFO_FILTER_FORMAT = "$.[?(@.stateCode=='%s')].code";

    /** Default API ID used for eGov RequestInfo header payloads. */
    public static final String DEFAULT_API_ID = "Rainmaker";
    /** Default locale used for eGov RequestInfo message correlation. */
    public static final String DEFAULT_LOCALE = "en_IN";

    // Separators & Common Symbols
    /** Separator pipe used in message IDs. */
    public static final String PIPE_SEPARATOR = "|";
    /** Separator dot used in hierarchical tenant IDs. */
    public static final String DOT_SEPARATOR = ".";
    /** URL Query parameter prefix for tenantId. */
    public static final String QUERY_PARAM_TENANT_ID = "?tenantId=";

    // Cache Names
    /** Cache name for active tenant IDs list. */
    public static final String CACHE_ACTIVE_TENANTS = "active_tenants";
    /** Cache name for active module detail records. */
    public static final String CACHE_TENANT_MODULE_DETAILS = "tenant_module_details";

    // Scheduler Audit Constants
    /** Scheduler name for daily ingestion job audit logging. */
    public static final String SCHEDULER_NAME_DAILY_INGESTION = "DAILY_INGESTION_SCHEDULER";
    /** Scheduler run status for running execution. */
    public static final String STATUS_RUNNING = "RUNNING";
    /** Scheduler run status for completed execution. */
    public static final String STATUS_COMPLETED = "COMPLETED";
    /** Scheduler run status for failed execution. */
    public static final String STATUS_FAILED = "FAILED";
}
