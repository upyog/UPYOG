package org.egov.refund.model;

import java.math.BigDecimal;

import org.egov.refund.util.JsonStringDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundAudit {
	private String id;
    private String refundId;

    
    private String refundNo;

    private String tenantId;
    private String moduleName;
    private String businessService;

    private String consumerCode;
    private String paymentId;

    private String applicantName;
    private String mobileNumber;

    private String refundCategory;
    private String refundReason;

    private String paymentModeOriginal;

    private BigDecimal amountPaid;
    private BigDecimal refundAmount;

    private String refundMode;

    private String status;

    private String sanctionRef;

    private Long financeApprovalDate;

    private String gatewayRefundId;
    
    private String fileStoreId;

    @JsonDeserialize(using = JsonStringDeserializer.class)
    private String beneficiaryDetails;
    
    @JsonDeserialize(using = JsonStringDeserializer.class)
    private String additionalDetails;

    private AuditDetails auditDetails;
    
    private Long auditCreatedTime;
    
    private String workflowAction;
    

}