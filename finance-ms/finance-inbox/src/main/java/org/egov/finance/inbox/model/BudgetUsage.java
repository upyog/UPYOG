
package org.egov.finance.inbox.model;

import java.sql.Timestamp;

import org.egov.finance.inbox.entity.BudgetDetail;

/**
 * @author eGov Model class for BudgetUsage
 */
public class BudgetUsage {
    public BudgetUsage()
    {
        super();
    }

    private Long id;
    private Integer financialYearId;
    private Integer moduleId;
    private String referenceNumber;
    private Double consumedAmount;
    private Double releasedAmount;
    private Timestamp updatedTime;
    private Integer createdby;
    private BudgetDetail budgetDetail;
    private String appropriationnumber;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id to set
     */
    private void setId(final Long id) {
        this.id = id;
    }

    /**
     * @return
     */
    public Integer getFinancialYearId() {
        return financialYearId;
    }

    /**
     * @param financialYearid
     */
    public void setFinancialYearId(final Integer financialYearId) {
        this.financialYearId = financialYearId;
    }

    /**
     * @return
     */
    public Integer getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId
     */
    public void setModuleId(final Integer moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * @param referenceNumber
     */
    public void setReferenceNumber(final String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /**
     * @return
     */
    public Double getConsumedAmount() {
        return consumedAmount;
    }

    /**
     * @param consumedAmount
     */
    public void setConsumedAmount(final Double consumedAmount) {
        this.consumedAmount = consumedAmount;
    }

    /**
     * @return
     */
    public Double getReleasedAmount() {
        return releasedAmount;
    }

    /**
     * @param releasedAmount
     */
    public void setReleasedAmount(final Double releasedAmount) {
        this.releasedAmount = releasedAmount;
    }

    /**
     * @return
     */
    public Timestamp getUpdatedTime() {
        return updatedTime;
    }

    /**
     * @param updatedTime
     */
    public void setUpdatedTime(final Timestamp updatedTime) {
        this.updatedTime = updatedTime;
    }

    /**
     * @return
     */
    public Integer getCreatedby() {
        return createdby;
    }

    /**
     * @param createdby
     */
    public void setCreatedby(final Integer createdby) {
        this.createdby = createdby;
    }

    /**
     * @return budgetDetail
     */
    public BudgetDetail getBudgetDetail() {
        return budgetDetail;
    }

    /**
     * @param budgetDetail the budgetDetail to set
     */
    public void setBudgetDetail(final BudgetDetail budgetDetail) {
        this.budgetDetail = budgetDetail;
    }

    public String getAppropriationnumber() {
        return appropriationnumber;
    }

    public void setAppropriationnumber(final String appropriationnumber) {
        this.appropriationnumber = appropriationnumber;
    }

}
