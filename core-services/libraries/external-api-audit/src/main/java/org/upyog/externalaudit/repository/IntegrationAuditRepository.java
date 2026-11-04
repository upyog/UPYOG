package org.upyog.externalaudit.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.upyog.externalaudit.constants.ExternalApiAuditConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * JDBC used only by reconciliation and cleanup jobs against {@code ug_external_api_*}.
 * Business request handling must not call this repository; persistence goes through Kafka + persister.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
@ConditionalOnBean(JdbcTemplate.class)
public class IntegrationAuditRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Sets {@code TIMED_OUT} on INITIATED rows whose {@code request_time} is older than the threshold.
     *
     * @return number of message_detail rows updated
     */
    public int markTimedOutInitiatedRequests(long requestTimeThreshold, long lastModifiedTime) {
        int updatedRows = jdbcTemplate.update(IntegrationAuditQueries.MARK_TIMED_OUT_INITIATED,
                ExternalApiAuditConstants.STATUS_TIMED_OUT,
                lastModifiedTime,
                ExternalApiAuditConstants.STATUS_INITIATED,
                requestTimeThreshold);
        if (updatedRows > 0) {
            log.info("Marked {} stale integration audit records as TIMED_OUT", updatedRows);
        }
        return updatedRows;
    }

    /**
     * Deletes expired rows from error, raw, then message tables (FK order).
     *
     * @return total rows deleted across the three tables
     */
    public int deleteExpiredAuditRecords(long createdTimeThreshold) {
        int errors = jdbcTemplate.update(IntegrationAuditQueries.DELETE_EXPIRED_ERROR_DETAIL, createdTimeThreshold);
        int raw = jdbcTemplate.update(IntegrationAuditQueries.DELETE_EXPIRED_RAW_DETAIL, createdTimeThreshold);
        int messages = jdbcTemplate.update(IntegrationAuditQueries.DELETE_EXPIRED_MESSAGE_DETAIL, createdTimeThreshold);
        int total = errors + raw + messages;
        if (total > 0) {
            log.info("Deleted expired integration audit rows. errors={}, raw={}, messages={}", errors, raw, messages);
        }
        return total;
    }
}
