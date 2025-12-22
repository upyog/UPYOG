package org.egov.web.notification.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.web.notification.sms.models.SmsTracker;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class SmsTrackerRowMapper implements RowMapper<SmsTracker> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SmsTracker mapRow(ResultSet rs, int rowNum) throws SQLException {

        try {
            return SmsTracker.builder()
                    .uuid(rs.getString("uuid"))
                    .amount(rs.getDouble("amount"))
                    .applicationNo(rs.getString("application_no"))
                    .tenantId(rs.getString("tenant_id"))
                    .service(rs.getString("service"))
                    .month(rs.getString("month"))
                    .year(rs.getString("year"))
                    .financialYear(rs.getString("financial_year"))
                    .fromDate(rs.getString("from_date"))
                    .toDate(rs.getString("to_date"))
                    .createdBy(rs.getString("created_by"))
                    .createdTime(rs.getLong("created_time"))
                    .lastModifiedBy(rs.getString("last_modified_by"))
                    .lastModifiedTime(rs.getLong("last_modified_time"))
                    .ward(rs.getString("ward"))
                    .billId(rs.getString("bill_id"))
                    .additionalDetail(rs.getString("additional_detail") != null? objectMapper.readTree(rs.getString("additional_detail")): null)
                    .smsRequest(rs.getString("sms_request") != null? objectMapper.readTree(rs.getString("sms_request")): null)
                    .smsResponse(rs.getString("sms_response") != null? objectMapper.readTree(rs.getString("sms_response")): null)
                    .resendCounter(rs.getShort("resend_counter"))
                    .smsStatus(rs.getBoolean("sms_status"))
                    .ownerMobileNo(rs.getString("owner_mobile_no"))
                    .ownerName(rs.getString("owner_name"))
                    .build();
        } catch (Exception e) {
            log.error("Error mapping SmsTracker row", e);
            throw new SQLException(e);
        }
    }
}
