package org.egov.model.budget;


import jdk.nashorn.internal.runtime.logging.Loggable;
import lombok.Getter;
import lombok.Setter;

import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Scheme;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.persistence.validator.annotation.Unique;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;



/**
 * Entity representing an individual budget line item in the financial system.
 *
 * <p>Each {@code BudgetItem} captures the budget estimates and actuals for a specific
 * combination of {@link CFunction} (department/function), {@link BudgetHead}, and
 * {@link CFinancialYear}. It maintains current year financial data alongside next year
 * estimates to support multi-year budget planning.</p>
 *
 * <p><b>Key Characteristics:</b></p>
 * <ul>
 *   <li>Links a budget head with a function and two financial years (current and target).</li>
 *   <li>Captures current year estimate, actual expenditure, and revised estimate.</li>
 *   <li>Captures the next year estimate for forward budget planning.</li>
 *   <li>Supports scheme-based budgeting via an optional {@link Scheme} association.</li>
 *   <li>Groups line items using a budget group code for classification.</li>
 *   <li>Generates a unique budget code from function and budget head codes.</li>
 *   <li>Associates with a {@link BudgetRegister} for the approval workflow.</li>
 *   <li>Provides null-safe getters that return {@link BigDecimal#ZERO} instead of {@code null}.</li>
 *   <li>Inherits audit trail support from {@link AbstractAuditable}.</li>
 * </ul>
 *
 * <p><b>Budget Item Lifecycle:</b></p>
 * <ol>
 *   <li>Created for a target financial year with an initial {@code nextEstimate}.</li>
 *   <li>Current year data ({@code currentEstimate}, {@code currentActual},
 *       {@code currentRevisedEstimate}) is populated as the financial year progresses.</li>
 *   <li>Associated with a {@link BudgetRegister} for workflow-based approval.</li>
 * </ol>
 *
 * <p>Mapped to the database table {@code egf_budgetitem}, with primary keys generated
 * from the sequence {@code seq_egf_budgetitem}.</p>
 *
 * @see BudgetHead
 * @see BudgetRegister
 * @see CFunction
 * @see CFinancialYear
 * @see AbstractAuditable
 */

@Entity
@Table(name = BudgetItem.TABLE_NAME)
@Unique(id = "id", tableName = BudgetItem.TABLE_NAME, enableDfltMsg = true)
@SequenceGenerator(name = BudgetItem.SEQ_BUDGET_ITEM, sequenceName = BudgetItem.SEQ_BUDGET_ITEM, allocationSize = 1)
@Setter
@Getter
public class BudgetItem extends AbstractAuditable {
    public static final String TABLE_NAME = "egf_budgetitem";
    public static final String SEQ_BUDGET_ITEM = "seq_egf_budgetitem";

    private static final Logger LOGGER = LoggerFactory.getLogger(BudgetItem.class);

    @Id
    @GeneratedValue(generator = SEQ_BUDGET_ITEM, strategy = GenerationType.SEQUENCE)
    private Long id;


    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "functionid")
    private CFunction function;

    // @Column(name = "functionid")
    // private Long function;

    @ManyToOne
    @JoinColumn(name = "budgetheadid")
    private BudgetHead budgetHead;

    @ManyToOne
    @JoinColumn(name = "financialyearid")
    private CFinancialYear financialYear; // budget being created for fy

    @ManyToOne
    @JoinColumn(name = "currentfinancialyearid")
    private CFinancialYear currentFinancialYear; // budget creating on fy

    @Column(name = "budgetcode")
    private String budgetCode;

    @NotNull
    @Column(name = "budgetgroup")
    private String budgetGroup;

    @NotNull
    @Column(name = "currentestimate", precision = 13, scale = 2)
    private BigDecimal currentEstimate;

    @NotNull
    @Column(name = "currentactual",  precision = 13, scale = 2)
    private BigDecimal currentActual;

    @NotNull
    @Column(name = "currentrevisedestimate",  precision = 13, scale = 2)
    private BigDecimal currentRevisedEstimate;

    @NotNull
    @Column(name = "nextestimate",  precision = 13, scale = 2)
    private BigDecimal nextEstimate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_register_id")
    private BudgetRegister budgetRegister;


    @ManyToOne
    @JoinColumn(name = "schemeid")
    private Scheme scheme;

    @Column(name = "statebudgetcode")
    private String stateBudgetCode;


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    private Integer rowIndex;


    @Column(name = "not_applicable", nullable = false)
    private Boolean notApplicable = false;


    public BigDecimal getSafeCurrentEstimate() {
         if (null != currentEstimate) {
             return  currentEstimate;
         } else {
             return BigDecimal.ZERO;
         }
    }

    public BigDecimal getSafeCurrentActual() {
        if (null != currentActual) {
            return  currentActual;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSafeCurrentRevisedEstimate() {
        if (null != currentRevisedEstimate) {
            return  currentRevisedEstimate;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSafeNextEstimate() {
        if (null != nextEstimate) {
            return  nextEstimate;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public Boolean isValuesFilled() {
        return currentEstimate != null && currentActual != null && currentRevisedEstimate != null && nextEstimate != null;
    }


    @Transient
    public String generateBudgetCode() {

        if (budgetHead == null || function == null) {
            return null;
        }

        String functionCode = function.getCode();
        String budgetHeadCode = budgetHead.getCode();

        if (functionCode == null || budgetHeadCode == null) {
            return null;
        }

        return functionCode + "-" + budgetHeadCode;
    }




}
