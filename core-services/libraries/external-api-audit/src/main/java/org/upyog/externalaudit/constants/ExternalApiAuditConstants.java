package org.upyog.externalaudit.constants;

/**
 * Status, direction, error-type, and named-integration constants written to
 * {@code ug_external_api_*} and used by consuming services.
 * <p>
 * Add a new {@code API_*} constant when onboarding another third-party integration
 * so {@code external_api_name} stays stable for reporting.
 * </p>
 */
public final class ExternalApiAuditConstants {

    private ExternalApiAuditConstants() {
    }

    /** UPYOG calling a third party. */
    public static final String DIRECTION_OUTBOUND = "OUTBOUND";

    /** Third party calling UPYOG. */
    public static final String DIRECTION_INBOUND = "INBOUND";

    public static final String STATUS_INITIATED = "INITIATED";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";

    /** Set by the reconciliation job when INITIATED never received SUCCESS/FAILED. */
    public static final String STATUS_TIMED_OUT = "TIMED_OUT";

    public static final String ERROR_TYPE_CLIENT = "CLIENT";
    public static final String ERROR_TYPE_SERVER = "SERVER";
    public static final String ERROR_TYPE_NETWORK = "NETWORK";
    public static final String ERROR_TYPE_TIMEOUT = "TIMEOUT";
    public static final String ERROR_TYPE_VALIDATION = "VALIDATION";

    /** National Dashboard inbound {@code /metric/_ingest} and per-row bulk ingest. */
    public static final String API_NATIONAL_DASHBOARD_METRIC_INGEST = "national-dashboard-metric-ingest";

    /** State systems posting PFMS transactions to DX {@code /v1/transactions/_create}. */
    public static final String API_STATE_PFMS_TRANSACTION_CREATE = "state-pfms-transaction-create";

    /** Outbound PFMS authentication. */
    public static final String API_PFMS_AUTH = "pfms-auth";

    /** Outbound PFMS voucher / data push. */
    public static final String API_PFMS_DATA_PUSH = "pfms-data-push";

    /** Raw JSONB envelope key for the integration URL or inbound path. */
    public static final String ENVELOPE_ENDPOINT = "endpoint";

    /** Raw JSONB envelope key for the HTTP method (existing name). */
    public static final String ENVELOPE_HTTP_METHOD = "httpMethod";

    /** Raw JSONB envelope key for the HTTP method (alias of {@link #ENVELOPE_HTTP_METHOD}). */
    public static final String ENVELOPE_METHOD = "method";

    public static final String PAYLOAD_TRUNCATED_MESSAGE = "Payload truncated due to size limit";

    /** Replacement value written for masked secret / PII fields. */
    public static final String MASKED_VALUE = "********";

    /** Used when the call site does not supply {@code tenantId}. */
    public static final String DEFAULT_TENANT = "unknown";

    /** ShedLock name so only one pod runs reconciliation. */
    public static final String RECONCILIATION_LOCK = "external-api-audit-reconciliation";

    /** ShedLock name so only one pod runs cleanup. */
    public static final String CLEANUP_LOCK = "external-api-audit-cleanup";
}
