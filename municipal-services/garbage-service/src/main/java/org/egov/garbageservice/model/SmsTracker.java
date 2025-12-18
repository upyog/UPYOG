package org.egov.garbageservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "sms_tracker")
public class SmsTracker {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "owner_mobile_no")
    private String mobileNumber;

    @Column(name = "sms_request", columnDefinition = "jsonb")
    private String smsRequest;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "application_no")
    private String applicationNo;

    @Column(name = "service")
    private String service;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private String year;

    @Column(name = "sms_status")
    private Boolean smsStatus;
    
    @Column(name = "financial_year")
    private String financialYear;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_time")
    private Long createdTime;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_time")
    private Long lastModifiedTime;

    @Column(name = "ward")
    private String ward;

    @Column(name = "bill_id")
    private String billId;

    @Column(name = "additional_detail", columnDefinition = "jsonb")
    private String additionalDetail;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "sms_response", columnDefinition = "jsonb")
    private String smsResponse;


    /* Getters and Setters */

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getSmsRequest() {
        return smsRequest;
    }

    public void setSmsRequest(String smsRequest) {
        this.smsRequest = smsRequest;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Boolean getSmsStatus() {
        return smsStatus;
    }

    public void setSmsStatus(Boolean smsStatus) {
        this.smsStatus = smsStatus;
    }
    
    public String getFinancialYear() { return financialYear; }
    public void setFinancialYear(String financialYear) { this.financialYear = financialYear; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Long getCreatedTime() { return createdTime; }
    public void setCreatedTime(Long createdTime) { this.createdTime = createdTime; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public Long getLastModifiedTime() { return lastModifiedTime; }
    public void setLastModifiedTime(Long lastModifiedTime) { this.lastModifiedTime = lastModifiedTime; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public String getAdditionalDetail() { return additionalDetail; }
    public void setAdditionalDetail(String additionalDetail) { this.additionalDetail = additionalDetail; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getSmsResponse() { return smsResponse; }
    public void setSmsResponse(String smsResponse) { this.smsResponse = smsResponse; }

}
