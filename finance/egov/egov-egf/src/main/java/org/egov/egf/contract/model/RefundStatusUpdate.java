package org.egov.egf.contract.model;

import java.math.BigDecimal;

public class RefundStatusUpdate {

	private String refundNo;

	// Original refund-service UUID required by the update API.
	private String id;

	private String tenantId;

	private String moduleName;

	private String businessService;

	private String consumerCode;

	private String paymentId;

	private BigDecimal refundAmount;

	private String refundCategory;

	private String refundReason;

	private String status;

	private String sanctionRef;

	private Long financeApprovalDate;

	private RefundStatusProcessInstance processInstance;

	public String getRefundNo() {
		return refundNo;
	}

	public void setRefundNo(final String refundNo) {
		this.refundNo = refundNo;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(final String tenantId) {
		this.tenantId = tenantId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(final String moduleName) {
		this.moduleName = moduleName;
	}

	public String getBusinessService() {
		return businessService;
	}

	public void setBusinessService(final String businessService) {
		this.businessService = businessService;
	}

	public String getConsumerCode() {
		return consumerCode;
	}

	public void setConsumerCode(final String consumerCode) {
		this.consumerCode = consumerCode;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(final String paymentId) {
		this.paymentId = paymentId;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(final BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getSanctionRef() {
		return sanctionRef;
	}

	public void setSanctionRef(final String sanctionRef) {
		this.sanctionRef = sanctionRef;
	}

	public Long getFinanceApprovalDate() {
		return financeApprovalDate;
	}

	public void setFinanceApprovalDate(final Long financeApprovalDate) {
		this.financeApprovalDate = financeApprovalDate;
	}

	public RefundStatusProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(final RefundStatusProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getRefundCategory() {
		return refundCategory;
	}

	public void setRefundCategory(final String refundCategory) {
		this.refundCategory = refundCategory;
	}

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(final String refundReason) {
		this.refundReason = refundReason;
	}
}