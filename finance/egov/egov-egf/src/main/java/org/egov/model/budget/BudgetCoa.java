package org.egov.model.budget;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.persistence.validator.annotation.Required;
import org.egov.model.bills.EgBillregister;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;



/**
 * Entity representing the mapping between a {@link BudgetHead} and its
 * corresponding Chart of Accounts (COA) entry.
 *
 * <p>Each record links a budget head to a specific GL account code and account
 * head name, enabling financial tracking and reporting against budgeted amounts.</p>
 *
 * <p><b>Key Characteristics:</b></p>
 * <ul>
 *   <li>Associates budget heads with chart of account entries via a many-to-one relationship.</li>
 *   <li>Maintains the GL account code and descriptive account head name for each mapping.</li>
 *   <li>Inherits audit trail support (created by, modified by, timestamps) from
 *       {@link AbstractAuditable}.</li>
 * </ul>
 *
 * <p>Mapped to the database table {@code EGF_BUDGETCOA}, with primary keys generated
 * from the sequence {@code SEQ_EGF_BUDGETCOA}.</p>
 *
 * @see BudgetHead
 * @see AbstractAuditable
 */

@Entity
@Table(name = "EGF_BUDGETCOA")
@SequenceGenerator(name = BudgetCoa.SEQ_BUDGETCOA, sequenceName = BudgetCoa.SEQ_BUDGETCOA, allocationSize = 1)

public class BudgetCoa extends AbstractAuditable {

    /** Sequence name for generating primary key */
    public static final String SEQ_BUDGETCOA = "SEQ_EGF_BUDGETCOA";
    private static final long serialVersionUID = 202519091745000L;

    /**
     * Primary key - Auto-generated using sequence
     */
    @Id
    @GeneratedValue(generator = SEQ_BUDGETCOA, strategy = GenerationType.SEQUENCE)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "budgethead_id")
    @NotNull
    private BudgetHead budgetHead;


    @Column(name = "account_code", nullable = false, length = 20)
    @NotNull
    @Length(max = 20)
    @SafeHtml
    private String accountCode;


    @Column(name = "account_head", nullable = false, length = 255)
    @NotNull
    @Length(max = 255)
    @SafeHtml
    private String accountHead;


    // --- Getters and Setters ---
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public BudgetHead getBudgetHead() {
        return budgetHead;
    }

    public void setBudgetHead(BudgetHead budgetHead) {
        this.budgetHead = budgetHead;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountHead() {
        return accountHead;
    }

    public void setAccountHead(String accountHead) {
        this.accountHead = accountHead;
    }
    
}
