package org.egov.receipt.consumer.model;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundKafkaDetail {

	private String id;
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
	@JsonDeserialize(using = JsonObjectOrStringDeserializer.class)
	private Object beneficiaryDetails;
	@JsonDeserialize(using = JsonObjectOrStringDeserializer.class)
	private Map<String, Object> additionalDetails;
	private Object auditDetails;
	private String fileStoreId;
	private RefundProcessInstance processInstance;
}