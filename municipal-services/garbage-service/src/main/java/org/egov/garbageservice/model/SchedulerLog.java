package org.egov.garbageservice.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchedulerLog {
    private String id;
    private String garbageAccountId;
    private String tenantId;
    private LocalDate billingDate;
    private Long billingPeriodFrom;
    private Long billingPeriodTo;
    private BigDecimal amount;
    private BigDecimal penaltyAmount;
    private String paymentType;
    private String status;
    private String createdBy;
    private Long createdTime;
    private String lastModifiedBy;
    private Long lastModifiedTime;
}