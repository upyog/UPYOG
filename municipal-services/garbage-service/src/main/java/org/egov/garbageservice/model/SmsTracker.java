package org.egov.garbageservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.math.BigDecimal;

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

    /**
     * Gets the uuid.
     *
     * @return the current uuid
     */

    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the uuid.
     *
     * @param uuid the uuid to set
     */

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the mobileNumber.
     *
     * @return the current mobileNumber
     */

    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * Sets the mobileNumber.
     *
     * @param mobileNumber the mobileNumber to set
     */

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * Gets the smsRequest.
     *
     * @return the current smsRequest
     */

    public String getSmsRequest() {
        return smsRequest;
    }

    /**
     * Sets the smsRequest.
     *
     * @param smsRequest the smsRequest to set
     */

    public void setSmsRequest(String smsRequest) {
        this.smsRequest = smsRequest;
    }

    /**
     * Gets the tenantId.
     *
     * @return the current tenantId
     */

    public String getTenantId() {
        return tenantId;
    }

    /**
     * Sets the tenantId.
     *
     * @param tenantId the tenantId to set
     */

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Gets the amount.
     *
     * @return the current amount
     */

    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     *
     * @param amount the amount to set
     */

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the applicationNo.
     *
     * @return the current applicationNo
     */

    public String getApplicationNo() {
        return applicationNo;
    }

    /**
     * Sets the applicationNo.
     *
     * @param applicationNo the applicationNo to set
     */

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    /**
     * Gets the service.
     *
     * @return the current service
     */

    public String getService() {
        return service;
    }

    /**
     * Sets the service.
     *
     * @param service the service to set
     */

    public void setService(String service) {
        this.service = service;
    }

    /**
     * Gets the month.
     *
     * @return the current month
     */

    public String getMonth() {
        return month;
    }

    /**
     * Sets the month.
     *
     * @param month the month to set
     */

    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Gets the year.
     *
     * @return the current year
     */

    public String getYear() {
        return year;
    }

    /**
     * Sets the year.
     *
     * @param year the year to set
     */

    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Gets the smsStatus.
     *
     * @return the current smsStatus
     */

    public Boolean getSmsStatus() {
        return smsStatus;
    }

    /**
     * Sets the smsStatus.
     *
     * @param smsStatus the smsStatus to set
     */

    public void setSmsStatus(Boolean smsStatus) {
        this.smsStatus = smsStatus;
    }

    /**
     * Gets the financialYear.
     *
     * @return the current financialYear
     */

    public String getFinancialYear() {
        return financialYear;
    }

    /**
     * Sets the financialYear.
     *
     * @param financialYear the financialYear to set
     */

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    /**
     * Gets the createdBy.
     *
     * @return the current createdBy
     */

    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the createdBy.
     *
     * @param createdBy the createdBy to set
     */

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the createdTime.
     *
     * @return the current createdTime
     */

    public Long getCreatedTime() {
        return createdTime;
    }

    /**
     * Sets the createdTime.
     *
     * @param createdTime the createdTime to set
     */

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * Gets the lastModifiedBy.
     *
     * @return the current lastModifiedBy
     */

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * Sets the lastModifiedBy.
     *
     * @param lastModifiedBy the lastModifiedBy to set
     */

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /**
     * Gets the lastModifiedTime.
     *
     * @return the current lastModifiedTime
     */

    public Long getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * Sets the lastModifiedTime.
     *
     * @param lastModifiedTime the lastModifiedTime to set
     */

    public void setLastModifiedTime(Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    /**
     * Gets the ward.
     *
     * @return the current ward
     */

    public String getWard() {
        return ward;
    }

    /**
     * Sets the ward.
     *
     * @param ward the ward to set
     */

    public void setWard(String ward) {
        this.ward = ward;
    }

    /**
     * Gets the billId.
     *
     * @return the current billId
     */

    public String getBillId() {
        return billId;
    }

    /**
     * Sets the billId.
     *
     * @param billId the billId to set
     */

    public void setBillId(String billId) {
        this.billId = billId;
    }

    /**
     * Gets the additionalDetail.
     *
     * @return the current additionalDetail
     */

    public String getAdditionalDetail() {
        return additionalDetail;
    }

    /**
     * Sets the additionalDetail.
     *
     * @param additionalDetail the additionalDetail to set
     */

    public void setAdditionalDetail(String additionalDetail) {
        this.additionalDetail = additionalDetail;
    }

    /**
     * Gets the ownerName.
     *
     * @return the current ownerName
     */

    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Sets the ownerName.
     *
     * @param ownerName the ownerName to set
     */

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * Gets the smsResponse.
     *
     * @return the current smsResponse
     */

    public String getSmsResponse() {
        return smsResponse;
    }

    /**
     * Sets the smsResponse.
     *
     * @param smsResponse the smsResponse to set
     */

    public void setSmsResponse(String smsResponse) {
        this.smsResponse = smsResponse;
    }

}
