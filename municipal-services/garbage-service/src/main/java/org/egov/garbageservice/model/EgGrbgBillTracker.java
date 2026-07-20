package org.egov.garbageservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import jakarta.persistence.Transient;

import org.egov.tracer.annotations.CustomSafeHtml;


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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getGrbgApplicationId() {
        return grbgApplicationId;
    }

    public void setGrbgApplicationId(String grbgApplicationId) {
        this.grbgApplicationId = grbgApplicationId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }
    
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
    
    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public BigDecimal getGrbgBillAmount() {
        return grbgBillAmount;
    }

    public void setGrbgBillAmount(BigDecimal grbgBillAmount) {
        this.grbgBillAmount = grbgBillAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }
    
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    
    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

}
