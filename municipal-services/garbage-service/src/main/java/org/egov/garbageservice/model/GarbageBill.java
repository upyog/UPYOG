package org.egov.garbageservice.model;

import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
/**
 * Local garbage bill record stored in garbage-service and linked to a garbage account.
 * Tracks bill reference, amounts (bill, arrear, penalty), payment status, and period metadata.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GarbageBill {

    private Long id;
    @CustomSafeHtml
    private String billRefNo;
    private Long garbageId;
    private Double billAmount;
    private Double arrearAmount;
    private Double paneltyAmount;
    private Double discountAmount;
    private Double totalBillAmount;
    private Double totalBillAmountAfterDueDate;
    @CustomSafeHtml
    private String billGeneratedBy;
    private Long billGeneratedDate;
    private Long billDueDate;
    @CustomSafeHtml
    private String billPeriod;
    private Double bankDiscountAmount;
    @CustomSafeHtml
    private String paymentId;
    @CustomSafeHtml
    private String paymentStatus;
    private AuditDetails auditDetails;
}
