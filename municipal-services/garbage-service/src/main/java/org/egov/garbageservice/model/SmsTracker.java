package org.egov.garbageservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

import org.egov.tracer.annotations.CustomSafeHtml;

@Entity
/**
 * JPA entity for eg_notification_sms_tracker storing outbound SMS queue state per bill.
 * Updated when GarbageSmsService processes pending bills for citizen reminders.
 */
@Table(name = "eg_notification_sms_tracker")
public class SmsTracker {

    @Id
    @Column(name = "uuid")
    @CustomSafeHtml
    private String uuid;

    @Column(name = "owner_mobile_no")
    @CustomSafeHtml
    private String mobileNumber;

    @Column(name = "sms_request", columnDefinition = "jsonb")
    @CustomSafeHtml
    private String smsRequest;

    @Column(name = "tenant_id")
    @CustomSafeHtml
    private String tenantId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "application_no")
    @CustomSafeHtml
    private String applicationNo;

    @Column(name = "service")
    @CustomSafeHtml
    private String service;

    @Column(name = "month")
    @CustomSafeHtml
    private String month;

    @Column(name = "year")
    @CustomSafeHtml
    private String year;

    @Column(name = "sms_status")
    private Boolean smsStatus;
    
    @Column(name = "financial_year")
    @CustomSafeHtml
    private String financialYear;

    @Column(name = "created_by")
    @CustomSafeHtml
    private String createdBy;

    @Column(name = "created_time")
    private Long createdTime;

    @Column(name = "last_modified_by")
    @CustomSafeHtml
    private String lastModifiedBy;

    @Column(name = "last_modified_time")
    private Long lastModifiedTime;

    @Column(name = "ward")
    @CustomSafeHtml
    private String ward;

    @Column(name = "bill_id")
    @CustomSafeHtml
    private String billId;

    @Column(name = "additional_detail", columnDefinition = "jsonb")
    @CustomSafeHtml
    private String additionalDetail;

    @Column(name = "owner_name")
    @CustomSafeHtml
    private String ownerName;

    @Column(name = "sms_response", columnDefinition = "jsonb")
    @CustomSafeHtml
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
