package org.egov.garbageservice.model;

import jakarta.persistence.*;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.math.BigDecimal;


@Entity
/**
 * JPA entity mapped to eg_grbg_bill_tracker for persisting bill generation tracker rows.
 * Database counterpart of the GrbgBillTracker domain model used by repositories.
 */
@Table(name = "eg_grbg_bill_tracker")
public class EgGrbgBillTracker {

    @Id
    @Column(name = "uuid")
    @CustomSafeHtml
    private String uuid;

    @Column(name = "grbg_application_id")
    @CustomSafeHtml
    private String grbgApplicationId;

    @Column(name = "tenant_id")
    @CustomSafeHtml
    private String tenantId;

    @Column(name = "month")
    @CustomSafeHtml
    private String month;

    @Column(name = "year")
    @CustomSafeHtml
    private String year;

    @Column(name = "from_date")
    @CustomSafeHtml
    private String fromDate;

    @Column(name = "to_date")
    @CustomSafeHtml
    private String toDate;

    @Column(name = "bill_id")
    @CustomSafeHtml
    private String billId;

    @Column(name = "grbg_bill_amount")
    private BigDecimal grbgBillAmount;

    @Column(name = "status")
    @CustomSafeHtml
    private String status;

    @Column(name = "bill_status")
    @CustomSafeHtml
    private String billStatus;

    @Column(name = "last_modified_by")
    @CustomSafeHtml
    private String lastModifiedBy;

    @Column(name = "ward")
    @CustomSafeHtml
    private String ward;

    @Transient
    @CustomSafeHtml
    private String mobileNumber;

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
     * Gets the grbgApplicationId.
     *
     * @return the current grbgApplicationId
     */

    public String getGrbgApplicationId() {
        return grbgApplicationId;
    }

    /**
     * Sets the grbgApplicationId.
     *
     * @param grbgApplicationId the grbgApplicationId to set
     */

    public void setGrbgApplicationId(String grbgApplicationId) {
        this.grbgApplicationId = grbgApplicationId;
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
     * Gets the grbgBillAmount.
     *
     * @return the current grbgBillAmount
     */

    public BigDecimal getGrbgBillAmount() {
        return grbgBillAmount;
    }

    /**
     * Sets the grbgBillAmount.
     *
     * @param grbgBillAmount the grbgBillAmount to set
     */

    public void setGrbgBillAmount(BigDecimal grbgBillAmount) {
        this.grbgBillAmount = grbgBillAmount;
    }

    /**
     * Gets the status.
     *
     * @return the current status
     */

    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status to set
     */

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the billStatus.
     *
     * @return the current billStatus
     */

    public String getBillStatus() {
        return billStatus;
    }

    /**
     * Sets the billStatus.
     *
     * @param billStatus the billStatus to set
     */

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
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
     * Gets the fromDate.
     *
     * @return the current fromDate
     */

    public String getFromDate() {
        return fromDate;
    }

    /**
     * Sets the fromDate.
     *
     * @param fromDate the fromDate to set
     */

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * Gets the toDate.
     *
     * @return the current toDate
     */

    public String getToDate() {
        return toDate;
    }

    /**
     * Sets the toDate.
     *
     * @param toDate the toDate to set
     */

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

}
