
package org.egov.finance.voucher.entity;

import static org.egov.finance.voucher.entity.Budget.SEQ_BUDGET;

import java.util.Date;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.egov.finance.voucher.validation.Unique;
import org.egov.finance.voucher.workflow.entity.State;
import org.egov.finance.voucher.workflow.entity.StateAware;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "EGF_BUDGET")
@SequenceGenerator(name = SEQ_BUDGET, sequenceName = SEQ_BUDGET, allocationSize = 1)
@Unique(fields = "name", enableDfltMsg = true)
public class Budget extends StateAware {

    public static final String SEQ_BUDGET = "SEQ_EGF_BUDGET";
    private static final long serialVersionUID = 3592259793739732756L;
    @Id
    @GeneratedValue(generator = SEQ_BUDGET, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Name should not be empty")
    @Length(max = 250, message = "Max 250 characters are allowed for description")
    @SafeHtml
    private String name;

    @SafeHtml
    @Length(max = 20)
    private String isbere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FINANCIALYEARID")
    @NotNull
    private FinancialYear financialYear;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent")
    private Budget parent;

    @Length(max = 250, message = "Max 250 characters are allowed for description")
    @SafeHtml
    private String description;

    @Column(name = "AS_ON_DATE")
    private Date asOnDate;

    private boolean isActiveBudget;

    private boolean isPrimaryBudget;

    @Length(max = 10, message = "Max 10 characters are allowed for description")
    @SafeHtml
    private String materializedPath;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "reference_budget")
    private Budget referenceBudget;

    @Column(name = "DOCUMENT_NUMBER")
    private Long documentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS")
    private EgwStatus status;

    @Transient
    @SafeHtml
    private String searchBere;


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public Budget getParent() {
        return parent;
    }

    public void setParent(final Budget parent) {
        this.parent = parent;
    }

    public Date getAsOnDate() {
        return asOnDate;
    }

    public void setAsOnDate(final Date asOnDate) {
        this.asOnDate = asOnDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String desc) {
        description = desc;
    }

    @NotNull(message = "Financial Year is required")
    public FinancialYear getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(final FinancialYear finYear) {
        financialYear = finYear;
    }

    @NotNull(message = "Name should not be empty")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return isbere
     */
    @NotNull(message = "BE/RE is required")
    public String getIsbere() {
        if (isbere == null)
            isbere = "BE";
        return isbere;
    }

    /**
     * @param isbere the isbere to set
     */
    public void setIsbere(final String isbere) {
        this.isbere = isbere;
    }

    /**
     * @return isActiveBudget
     */
    public boolean getIsActiveBudget() {
        return isActiveBudget;
    }

    /**
     * @param isActiveBudget the isActiveBudget to set
     */
    public void setIsActiveBudget(final boolean isActiveBudget) {
        this.isActiveBudget = isActiveBudget;
    }

    /**
     * @return isPrimaryBudget
     */
    public boolean getIsPrimaryBudget() {
        return isPrimaryBudget;
    }

    /**
     * @param isPrimaryBudget the isPrimaryBudget to set
     */
    public void setIsPrimaryBudget(final boolean isPrimaryBudget) {
        this.isPrimaryBudget = isPrimaryBudget;
    }

    @Override
    public String getStateDetails() {
        return name;
    }

    /**
     * @return the materialized_path
     */
    public String getMaterializedPath() {
        return materializedPath;
    }

    /**
     * @param materialized_path the materialized_path to set
     */
    public void setMaterializedPath(final String materializedPath) {
        this.materializedPath = materializedPath;
    }

    public Budget getReferenceBudget() {
        return referenceBudget;
    }

    public void setReferenceBudget(final Budget reference) {
        referenceBudget = reference;
    }

    public Long getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(final Long documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Override
    public String myLinkId() {
        return getId().toString();
    }

    public EgwStatus getStatus() {
        return status;
    }

    public void setStatus(EgwStatus status) {
        this.status = status;
    }

    public void setWfState(State state) {
        //Won't work
    }

    public String getSearchBere() {
        return searchBere;
    }

    public void setSearchBere(String searchBere) {
        this.searchBere = searchBere;
    }

}
