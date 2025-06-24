package org.egov.finance.voucher.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.voucher.entity.BudgetDetail;
import org.egov.finance.voucher.entity.EgwStatus;
import org.egov.finance.voucher.workflow.entity.StateAware;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "egf_budget_reappropriation") 
public class BudgetReAppropriation extends StateAware implements Serializable {

    private static final long serialVersionUID = 2343135780753283100L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budgetdetail_id")
    private BudgetDetail budgetDetail;

    @Column(name = "addition_amount")
    private BigDecimal additionAmount = BigDecimal.ZERO;

    @Column(name = "deduction_amount")
    private BigDecimal deductionAmount = BigDecimal.ZERO;

    @Column(name = "original_addition_amount")
    private BigDecimal originalAdditionAmount = BigDecimal.ZERO;

    @Column(name = "original_deduction_amount")
    private BigDecimal originalDeductionAmount = BigDecimal.ZERO;

    @Column(name = "anticipatory_amount")
    private BigDecimal anticipatoryAmount = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private EgwStatus status;

    @Temporal(TemporalType.DATE)
    @Column(name = "as_on_date")
    private Date asOnDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reappropriation_misc_id")
    private BudgetReAppropriationMisc reAppropriationMisc;
}
