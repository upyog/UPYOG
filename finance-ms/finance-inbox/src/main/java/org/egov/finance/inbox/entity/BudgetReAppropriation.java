
package org.egov.finance.inbox.entity;

import static org.egov.finance.inbox.entity.BudgetReAppropriation.SEQ_BUDGETAPPRO;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.inbox.workflow.entity.StateAware;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;


@Entity
@Table(name = "egf_budget_reappropriation")
@SequenceGenerator(name = SEQ_BUDGETAPPRO, sequenceName = SEQ_BUDGETAPPRO, allocationSize = 1)
public class BudgetReAppropriation extends StateAware {
	private static final long serialVersionUID = 2343135780753283100L;
	
	public static final String SEQ_BUDGETAPPRO = "SEQ_BUDGETAPPRO";
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_BUDGETAPPRO)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budgetdetail")
	private BudgetDetail budgetDetail;
	
	private BigDecimal additionAmount = new BigDecimal("0.0");
	private BigDecimal deductionAmount = new BigDecimal("0.0");
	private BigDecimal originalAdditionAmount = new BigDecimal("0.0");
	private BigDecimal originalDeductionAmount = new BigDecimal("0.0");
	private BigDecimal anticipatoryAmount = new BigDecimal("0.0");
	private EgwStatus status;
	private Date asOnDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reappropriationmiscid")
	private BudgetReAppropriationMisc reAppropriationMisc;

	public EgwStatus getStatus() {
		return status;
	}

	public void setStatus(final EgwStatus status) {
		this.status = status;
	}

	public BudgetReAppropriationMisc getReAppropriationMisc() {
		return reAppropriationMisc;
	}

	public void setReAppropriationMisc(final BudgetReAppropriationMisc reAppropriationMisc) {
		this.reAppropriationMisc = reAppropriationMisc;
	}

	public BigDecimal getAnticipatoryAmount() {
		return anticipatoryAmount;
	}

	public void setAnticipatoryAmount(final BigDecimal anticipatoryAmount) {
		this.anticipatoryAmount = anticipatoryAmount;
	}

	public BudgetDetail getBudgetDetail() {
		return budgetDetail;
	}

	public void setBudgetDetail(final BudgetDetail budgetDetail) {
		this.budgetDetail = budgetDetail;
	}

	public BigDecimal getAdditionAmount() {
		return additionAmount;
	}

	public void setAdditionAmount(final BigDecimal additionAmount) {
		this.additionAmount = additionAmount;
	}

	public BigDecimal getDeductionAmount() {
		return deductionAmount;
	}

	public void setDeductionAmount(final BigDecimal deductionAmount) {
		this.deductionAmount = deductionAmount;
	}

	/*
	 * @Override public Long getId() { return id; }
	 * 
	 * @Override protected void setId(final Long id) { this.id = id; }
	 */
	@Override
	public String getStateDetails() {
		return null;
	}

	public BigDecimal getOriginalAdditionAmount() {
		return originalAdditionAmount;
	}

	public void setOriginalAdditionAmount(final BigDecimal originalAdditionAmount) {
		this.originalAdditionAmount = originalAdditionAmount;
	}

	public BigDecimal getOriginalDeductionAmount() {
		return originalDeductionAmount;
	}

	public void setOriginalDeductionAmount(final BigDecimal originalDeductionAmount) {
		this.originalDeductionAmount = originalDeductionAmount;
	}

	public Date getAsOnDate() {
		return asOnDate;
	}

	public void setAsOnDate(final Date asOnDate) {
		this.asOnDate = asOnDate;
	}

}
