package org.egov.model.refund;

import java.math.BigDecimal;

import org.egov.infra.persistence.entity.Auditable;
import org.egov.infra.workflow.entity.StateAware;

public class RefundApplication extends StateAware implements Auditable {

	private static final long serialVersionUID = 1L;

	private Long id;

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

	private String status;

	private String voucherNumber;

	private String rejectionReason;

	private String fundCode;

	private String departmentCode;

	private String functionCode;

	@Override
	public String getStateDetails() {
		return refundApplicationNumber;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	protected void setId(final Long id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(final String tenantId) {
		this.tenantId = tenantId;
	}

	public String getRefundApplicationNumber() {
		return refundApplicationNumber;
	}

	public void setRefundApplicationNumber(final String refundApplicationNumber) {

		this.refundApplicationNumber = refundApplicationNumber;
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

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(final String referenceNumber) {

		this.referenceNumber = referenceNumber;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(final String paymentId) {
		this.paymentId = paymentId;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(final String receiptNumber) {

		this.receiptNumber = receiptNumber;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(final BigDecimal refundAmount) {

		this.refundAmount = refundAmount;
	}

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(final String refundReason) {

		this.refundReason = refundReason;
	}

	public Long getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(final Long refundDate) {
		this.refundDate = refundDate;
	}

	public String getDebitGlCode() {
		return debitGlCode;
	}

	public void setDebitGlCode(final String debitGlCode) {

		this.debitGlCode = debitGlCode;
	}

	public String getCreditGlCode() {
		return creditGlCode;
	}

	public void setCreditGlCode(final String creditGlCode) {

		this.creditGlCode = creditGlCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(final String voucherNumber) {

		this.voucherNumber = voucherNumber;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(final String rejectionReason) {

		this.rejectionReason = rejectionReason;
	}

	public String getFundCode() {
		return fundCode;
	}

	public void setFundCode(final String fundCode) {
		this.fundCode = fundCode;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(final String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(final String functionCode) {
		this.functionCode = functionCode;
	}
}