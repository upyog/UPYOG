package org.egov.receipt.consumer.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundFinanceApplication {

    private String id;

    private String tenantId;

    private String refundApplicationNumber;

    private String moduleName;

    private String businessService;

    private String referenceNumber;

    private String paymentId;

    private String receiptNumber;

    private BigDecimal refundAmount;

    private String refundReason;

    private Long refundDate;

    private String debitGlCode;

    private String creditGlCode;

    private String fundCode;

    private String departmentCode;

    private String functionCode;

    /*
    * Finance application status.
    */
    private String status;

    /*
     * Finance voucher created after approval.
     */
    private String voucherNumber;

    private AuditDetails auditDetails;
}