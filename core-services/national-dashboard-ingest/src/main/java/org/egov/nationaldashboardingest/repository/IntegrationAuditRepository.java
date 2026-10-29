package org.egov.nationaldashboardingest.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.nationaldashboardingest.utils.ExternalApiAuditConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class IntegrationAuditRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int markTimedOutInitiatedRequests(long requestTimeThreshold, long lastModifiedTime) {
        String sql = """
                UPDATE ug_external_api_message_detail
                SET status = ?, last_modified_time = ?
                WHERE status = ? AND request_time < ?
                """;
        int updatedRows = jdbcTemplate.update(sql,
                ExternalApiAuditConstants.STATUS_TIMED_OUT,
                lastModifiedTime,
                ExternalApiAuditConstants.STATUS_INITIATED,
                requestTimeThreshold);
        if (updatedRows > 0) {
            log.info("Marked {} stale integration audit records as TIMED_OUT", updatedRows);
        }
        return updatedRows;
    }
}
