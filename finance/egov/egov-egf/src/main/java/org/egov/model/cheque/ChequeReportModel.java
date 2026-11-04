package org.egov.model.cheque;

import java.util.Date;

import jakarta.validation.constraints.NotNull;

import org.egov.infra.persistence.validator.annotation.DateFormat;
import org.egov.infra.validation.SanitizeHtml;

public class ChequeReportModel {

	private int id;
	private int fundId;
	@SanitizeHtml
	private String bankBranchId;
	private int bankAccountId;
	@SanitizeHtml
	private String bankBranch;
	@SanitizeHtml
	private String bankAccountNumber;
	@SanitizeHtml
	private String surrenderReason;
	@NotNull
	@DateFormat
	private Date fromDate;
	@NotNull
	@DateFormat
	private Date toDate;
	@SanitizeHtml
	private String chequeNumber;
	@DateFormat
	private Date chequeDate;
	@SanitizeHtml
	private String payTo;
	@SanitizeHtml
	private String voucherNumber;
	@DateFormat
	private Date voucherDate;
	private Long voucherHeaderId;

	public ChequeReportModel() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public int getFundId() {
		return fundId;
	}

	public void setFundId(int fundId) {
		this.fundId = fundId;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getSurrenderReason() {
		return surrenderReason;
	}

	public void setSurrenderReason(String surrenderReason) {
		this.surrenderReason = surrenderReason;
	}

	public String getBankBranchId() {
		return bankBranchId;
	}

	public void setBankBranchId(String bankBranchId) {
		this.bankBranchId = bankBranchId;
	}

	public int getBankAccountId() {
		return bankAccountId;
	}

	public void setBankAccountId(int bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public String getChequeNumber() {
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getPayTo() {
		return payTo;
	}

	public void setPayTo(String payTo) {
		this.payTo = payTo;
	}

	public String getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public Date getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(Date voucherDate) {
		this.voucherDate = voucherDate;
	}

	public Long getVoucherHeaderId() {
		return voucherHeaderId;
	}

	public void setVoucherHeaderId(Long voucherHeaderId) {
		this.voucherHeaderId = voucherHeaderId;
	}

}
