package org.egov.garbageservice.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Audit entity logging execution details, start/end timestamps, success status, and error logs for scheduled background jobs.
 */
@Data
@Builder
public class SchedulerLog {
    private String id;
    private String garbageAccountId;
    private String tenantId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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