package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GarbageBill {

    private Long id;
    private String billRefNo;
    private Long garbageId;
    private Double billAmount;
    private Double arrearAmount;
    private Double paneltyAmount;
    private Double discountAmount;
    private Double totalBillAmount;
    private Double totalBillAmountAfterDueDate;
    private String billGeneratedBy;
    private Long billGeneratedDate;
    private Long billDueDate;
    private String billPeriod;
    private Double bankDiscountAmount;
    private String paymentId;
    private String paymentStatus;
    private AuditDetails auditDetails;
}
