package org.egov.edcr.contract;

import java.util.Date;
import java.util.List;

import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.PlanBpa;
/**
 * Represents the complete details of an eDCR (Electronic Development Control
 * Regulations) BPA application.
 *
 * <p>
 * This DTO is used to transfer application-related information between
 * different layers of the system, including application metadata, plan
 * details, permit information, uploaded files, validation results, and
 * processing status.
 * </p>
 *
 * <p>
 * The class encapsulates:
 * <ul>
 * <li>Application details such as application number, date, type, and status.</li>
 * <li>eDCR-specific information such as eDCR number and comparison eDCR number.</li>
 * <li>Plan-related information including plan reports, generated PDFs, and BPA plan details.</li>
 * <li>Permit details such as permit number and permit issue date.</li>
 * <li>DXF file references used during plan scrutiny and processing.</li>
 * <li>Error information generated during validation and scrutiny processes.</li>
 * <li>Tenant-specific information for multi-tenant deployments.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This object is commonly used in eDCR workflows for application submission,
 * scrutiny, approval processing, report generation, and BPA integration.
 * </p>
 */
public class EdcrDetailBpa {

    private String dxfFile;

    private String updatedDxfFile;

    private String planReport;

    private String transactionNumber;

    private Date applicationDate;

    private String applicationNumber;

    private String status;

    private String edcrNumber;

    private String tenantId;

    private String errors;

    private List<String> planPdfs;

    private PlanBpa planDetail;

    private String permitNumber;

    private Date permitDate;

    private String appliactionType;

    private String applicationSubType;

    private String comparisonEdcrNumber;

    public String getPlanReport() {
        return planReport;
    }

    public void setPlanReport(String planReport) {
        this.planReport = planReport;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PlanBpa getPlanDetail() {
        return planDetail;
    }

    public void setPlanDetail(PlanBpa planDetail) {
        this.planDetail = planDetail;
    }

    public List<String> getPlanPdfs() {
        return planPdfs;
    }

    public void setPlanPdfs(List<String> planPdfs) {
        this.planPdfs = planPdfs;
    }

    public String getEdcrNumber() {
        return edcrNumber;
    }

    public void setEdcrNumber(String edcrNumber) {
        this.edcrNumber = edcrNumber;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDxfFile() {
        return dxfFile;
    }

    public void setDxfFile(String dxfFile) {
        this.dxfFile = dxfFile;
    }

    public String getUpdatedDxfFile() {
        return updatedDxfFile;
    }

    public void setUpdatedDxfFile(String updatedDxfFile) {
        this.updatedDxfFile = updatedDxfFile;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getPermitNumber() {
        return permitNumber;
    }

    public void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public Date getPermitDate() {
        return permitDate;
    }

    public void setPermitDate(Date permitDate) {
        this.permitDate = permitDate;
    }

    public String getAppliactionType() {
        return appliactionType;
    }

    public void setAppliactionType(String appliactionType) {
        this.appliactionType = appliactionType;
    }

    public String getApplicationSubType() {
        return applicationSubType;
    }

    public void setApplicationSubType(String applicationSubType) {
        this.applicationSubType = applicationSubType;
    }

    public String getComparisonEdcrNumber() {
        return comparisonEdcrNumber;
    }

    public void setComparisonEdcrNumber(String comparisonEdcrNumber) {
        this.comparisonEdcrNumber = comparisonEdcrNumber;
    }

    @Override
    public String toString() {
        return "EdcrDetail [transactionNumber=" + transactionNumber + ", applicationDate=" + applicationDate
                + ", applicationNumber=" + applicationNumber + ", status=" + status + ", edcrNumber=" + edcrNumber + ", tenantId="
                + tenantId + ", errors=" + errors + ", planPdfs=" + planPdfs + ", planDetail=" + planDetail + ", permitNumber="
                + permitNumber + ", permitDate=" + permitDate + ", appliactionType=" + appliactionType + ", applicationSubType="
                + applicationSubType + ", comparisonEdcrNumber=" + comparisonEdcrNumber + "]";
    }

}
