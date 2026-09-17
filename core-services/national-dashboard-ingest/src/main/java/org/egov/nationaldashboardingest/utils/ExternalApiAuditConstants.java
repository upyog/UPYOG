package org.egov.nationaldashboardingest.utils;

public final class ExternalApiAuditConstants {

    private ExternalApiAuditConstants() {
    }

    public static final String DIRECTION_OUTBOUND = "OUTBOUND";
    public static final String DIRECTION_INBOUND = "INBOUND";

    public static final String STATUS_INITIATED = "INITIATED";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_TIMED_OUT = "TIMED_OUT";

    public static final String ERROR_TYPE_CLIENT = "CLIENT";
    public static final String ERROR_TYPE_SERVER = "SERVER";
    public static final String ERROR_TYPE_NETWORK = "NETWORK";
    public static final String ERROR_TYPE_TIMEOUT = "TIMEOUT";
    public static final String ERROR_TYPE_VALIDATION = "VALIDATION";

    public static final String API_NATIONAL_DASHBOARD_METRIC_INGEST = "national-dashboard-metric-ingest";

    public static final String PAYLOAD_TRUNCATED_MESSAGE = "Payload truncated due to size limit";
}
