package org.upyog.externalaudit.repository;

/**
 * JDBC used only by reconciliation and cleanup jobs against {@code ug_external_api_*}.
 */
public final class IntegrationAuditQueries {

    private IntegrationAuditQueries() {
    }

    public static final String MARK_TIMED_OUT_INITIATED = """
            UPDATE ug_external_api_message_detail
            SET status = ?, last_modified_time = ?
            WHERE status = ? AND request_time < ?
            """;

    public static final String DELETE_EXPIRED_ERROR_DETAIL = """
            DELETE FROM ug_external_api_error_detail e
            USING ug_external_api_message_detail m
            WHERE e.correlation_id = m.correlation_id AND m.created_time < ?
            """;

    public static final String DELETE_EXPIRED_RAW_DETAIL = """
            DELETE FROM ug_external_api_message_raw_detail r
            USING ug_external_api_message_detail m
            WHERE r.correlation_id = m.correlation_id AND m.created_time < ?
            """;

    public static final String DELETE_EXPIRED_MESSAGE_DETAIL = """
            DELETE FROM ug_external_api_message_detail
            WHERE created_time < ?
            """;
}
