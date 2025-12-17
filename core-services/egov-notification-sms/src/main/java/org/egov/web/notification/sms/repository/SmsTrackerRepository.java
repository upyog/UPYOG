package org.egov.web.notification.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.web.notification.sms.models.SmsTracker;
import org.egov.web.notification.sms.repository.SMSTemplateQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.postgresql.util.PGobject;
import java.text.SimpleDateFormat;
import java.util.List;


@Repository
@Slf4j
public class SmsTrackerRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

   public void insert(SmsTracker tracker) {

    PGobject additionalDetailJson = null;
    PGobject smsRequestJson = null;
    PGobject smsResponseJson = null;

    try {
        if (tracker.getAdditionalDetail() != null) {
            additionalDetailJson = new PGobject();
            additionalDetailJson.setType("jsonb");
            additionalDetailJson.setValue(objectMapper.writeValueAsString(tracker.getAdditionalDetail()));
        }

        if (tracker.getSmsRequest() != null) {
            smsRequestJson = new PGobject();
            smsRequestJson.setType("jsonb");
            smsRequestJson.setValue(objectMapper.writeValueAsString(tracker.getSmsRequest()));
        }

        if (tracker.getSmsResponse() != null) {
            smsResponseJson = new PGobject();
            smsResponseJson.setType("jsonb");
            smsResponseJson.setValue(objectMapper.writeValueAsString(tracker.getSmsResponse()));
        }
    } catch (Exception e) {
        log.error("Error converting JSON fields to jsonb", e);
    }
    
    
    Object[] params = new Object[]{
            tracker.getUuid(),
            tracker.getAmount(),
            tracker.getApplicationNo(),
            tracker.getTenantId(),
            tracker.getService(),
            tracker.getMonth(),
            tracker.getYear(),
            tracker.getFinancialYear(),
            tracker.getFromDate(),
            tracker.getToDate(),  
            tracker.getCreatedBy(),
            tracker.getCreatedTime(),
            tracker.getLastModifiedBy(),
            tracker.getLastModifiedTime(),
            tracker.getWard(),
            tracker.getBillId(),
            additionalDetailJson,
            tracker.getSmsStatus() != null ? tracker.getSmsStatus() : false,
            smsRequestJson,
            smsResponseJson,
            tracker.getOwnerMobileNo(),
            tracker.getOwnerName()
    };

    jdbcTemplate.update(SMSTemplateQueryBuilder.INSERT_SMS_TRACKER, params);
}
   
   public List<SmsTracker> fetchPendingSms() {

	    String query = "SELECT * FROM sms_tracker WHERE sms_status = false LIMIT 2000";

	    return jdbcTemplate.query(query, new SmsTrackerRowMapper());
	}
   
   public void updateSmsStatus(SmsTracker tracker) {
       String query = "UPDATE sms_tracker SET sms_status = true, sms_response = ? WHERE uuid = ?";
       PGobject smsResponseJson = null;
       try {
           if (tracker.getSmsResponse() != null) {
               smsResponseJson = new PGobject();
               smsResponseJson.setType("jsonb");
               smsResponseJson.setValue(objectMapper.writeValueAsString(tracker.getSmsResponse()));
           }
       } catch (Exception e) {
           log.error("Error converting SMS response to jsonb", e);
       }
       jdbcTemplate.update(query, smsResponseJson, tracker.getUuid());
   }
}

