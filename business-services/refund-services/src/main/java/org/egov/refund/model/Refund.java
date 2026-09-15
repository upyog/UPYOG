package org.egov.refund.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refund {

    private UUID id;
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

    private LocalDateTime financeApprovalDate;

    private String gatewayRefundId;

    private Map<String, Object> beneficiaryDetails;
    private Map<String, Object> additionalDetails;
    
    private AuditDetails auditDetails;
    
    private String fileStoreId;
    
    private RefundProcessInstance processInstance;
    
  }