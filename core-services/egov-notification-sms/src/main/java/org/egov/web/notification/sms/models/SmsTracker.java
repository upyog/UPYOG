package org.egov.web.notification.sms.models;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmsTracker {

    private String uuid;
    private Double amount;
    private String applicationNo;
    private String tenantId;
    private String service;
    private String month;
    private String year;
    private String financialYear;
    private String fromDate;
    private String toDate;

    private String createdBy;
    private Long createdTime;
    private String lastModifiedBy;
    private Long lastModifiedTime;

    private String ward;
    private String billId;

    private JsonNode additionalDetail;
    private Boolean smsStatus;
    
    private JsonNode smsRequest;
    private JsonNode smsResponse;
    private String ownerMobileNo;
    private String ownerName;
    
    private Short resendCounter;

}
