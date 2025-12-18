package org.egov.garbageservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import javax.persistence.Transient;


@Entity
@Table(name = "eg_grbg_bill_tracker")
public class EgGrbgBillTracker {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "grbg_application_id")
    private String grbgApplicationId;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private String year;
    
    @Column(name = "from_date")
    private String fromDate;
    
    @Column(name = "to_date")
    private String toDate;

    @Column(name = "bill_id")
    private String billId;

    @Column(name = "grbg_bill_amount")
    private BigDecimal grbgBillAmount;

    @Column(name = "status")
    private String status;

    @Column(name = "bill_status")
    private String billStatus;
    
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
    
    @Column(name = "ward")
    private String ward;
    
    @Transient
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
