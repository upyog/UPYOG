package org.egov.model.remittance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.egov.infra.persistence.validator.annotation.DateFormat;
import org.egov.infra.validation.SanitizeHtml;

public class RemittanceReportModel {
    private Long id;
    private int srNo;
    private Boolean selected;
    @SanitizeHtml
    private String receiptId;
    @SanitizeHtml
    private String receiptDate;
    @SanitizeHtml
    private String receiptNumber;
    private BigDecimal instrumentAmount;
    @SanitizeHtml
    private String service;
    @SanitizeHtml
    private String instrumentType;
    @SanitizeHtml
    private String instrumentNumber;
    @SanitizeHtml
    private String instrumentDate;
    @SanitizeHtml
    private String instrumentId;
    @SanitizeHtml
    private String fund;
    @SanitizeHtml
    private String department;
    @SanitizeHtml
    private String fundName;
    @SanitizeHtml
    private String departmentName;
    @SanitizeHtml
    private String serviceName;
    @SanitizeHtml
    private String bankBranch;
    @SanitizeHtml
    private String bank;
    @SanitizeHtml
    private String remittanceReferenceNumber;
    @SanitizeHtml
    private String bankAccount;
    private Long financialYear;
    @DateFormat
    private Date fromDate;
    @DateFormat
    private Date toDate;
    @SanitizeHtml
    private String remittedOn;
    @SanitizeHtml
    private String remitterId;
    @SanitizeHtml
    private String remittedBy;
    @SanitizeHtml
    private String transactionNumber;
    @SanitizeHtml
    private String payee;
    @SanitizeHtml
    private String drawer;
    @SanitizeHtml
    private String createdBy;
    private int totalCount;
    private List linkedRemittedList;
    @SanitizeHtml
    private String receiptSourceUrl;
    @SanitizeHtml
    private String ifscCode;
    public Boolean getSelected() {
        return selected;
    }
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
    public String getReceiptId() {
        return receiptId;
    }
    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }
    public String getReceiptDate() {
        return receiptDate;
    }
    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }
    public String getReceiptNumber() {
        return receiptNumber;
    }
    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
    public BigDecimal getInstrumentAmount() {
        return instrumentAmount;
    }
    public void setInstrumentAmount(BigDecimal instrumentAmount) {
        this.instrumentAmount = instrumentAmount;
    }
    public String getService() {
        return service;
    }
    public void setService(String service) {
        this.service = service;
    }
    public String getInstrumentType() {
        return instrumentType;
    }
    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }
    public String getInstrumentNumber() {
        return instrumentNumber;
    }
    public void setInstrumentNumber(String instrumentNumber) {
        this.instrumentNumber = instrumentNumber;
    }
    public String getInstrumentDate() {
        return instrumentDate;
    }
    public void setInstrumentDate(String instrumentDate) {
        this.instrumentDate = instrumentDate;
    }
    public String getInstrumentId() {
        return instrumentId;
    }
    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }
    public String getFund() {
        return fund;
    }
    public void setFund(String fund) {
        this.fund = fund;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getFundName() {
        return fundName;
    }
    public void setFundName(String fundName) {
        this.fundName = fundName;
    }
    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public String getBankBranch() {
        return bankBranch;
    }
    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }
    public String getBank() {
        return bank;
    }
    public void setBank(String bank) {
        this.bank = bank;
    }
    public String getRemittanceReferenceNumber() {
        return remittanceReferenceNumber;
    }
    public void setRemittanceReferenceNumber(String remittanceReferenceNumber) {
        this.remittanceReferenceNumber = remittanceReferenceNumber;
    }
    public String getBankAccount() {
        return bankAccount;
    }
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }
    public Long getFinancialYear() {
        return financialYear;
    }
    public void setFinancialYear(Long financialYear) {
        this.financialYear = financialYear;
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
    public void setRemitterId(String remitterId) {
        this.remitterId = remitterId;
    }
    public String getRemitterId() {
        return remitterId;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getSrNo() {
        return srNo;
    }
    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }
    public String getTransactionNumber() {
        return transactionNumber;
    }
    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }
    public String getPayee() {
        return payee;
    }
    public void setPayee(String payee) {
        this.payee = payee;
    }
    public String getDrawer() {
        return drawer;
    }
    public void setDrawer(String drawer) {
        this.drawer = drawer;
    }
    public String getRemittedOn() {
        return remittedOn;
    }
    public void setRemittedOn(String remittedOn) {
        this.remittedOn = remittedOn;
    }
    public String getRemittedBy() {
        return remittedBy;
    }
    public void setRemittedBy(String remittedBy) {
        this.remittedBy = remittedBy;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public int getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    public List getLinkedRemittedList() {
        return linkedRemittedList;
    }
    public void setLinkedRemittedList(List linkedRemittedList) {
        this.linkedRemittedList = linkedRemittedList;
    }
    public String getReceiptSourceUrl() {
        return receiptSourceUrl;
    }
    public void setReceiptSourceUrl(String receiptSourceUrl) {
        this.receiptSourceUrl = receiptSourceUrl;
    }
    public String getIfscCode() {
        return ifscCode;
    }
    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }
}
