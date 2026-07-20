package org.egov.model.budget;

import lombok.Getter;
import lombok.Setter;
import org.egov.commons.CFinancialYear;
import org.egov.commons.EgwStatus;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.workflow.entity.StateAware;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Entity representing a budget register document in the financial system.
 *
 * <p>A {@code BudgetRegister} acts as a container that groups multiple {@link BudgetItem}
 * entries together for batch processing through a workflow-based approval lifecycle.
 * It serves as the primary entry point for budget creation and submission in the system.</p>
 *
 * <p><b>Key Characteristics:</b></p>
 * <ul>
 *   <li>Carries a unique, non-updatable register number for tracking and reference.</li>
 *   <li>Groups one or more {@link BudgetItem} records via a one-to-many relationship,
 *       with cascade and orphan-removal behaviour.</li>
 *   <li>Extends {@link StateAware} to participate in the eGov workflow engine,
 *       enabling state transitions and approval routing.</li>
 *   <li>Links both the authoring financial year ({@code currentFinancialYear}) and
 *       the target financial year ({@code financialYear}) for multi-year budget planning.</li>
 *   <li>Tracks the approval status via {@link EgwStatus}.</li>
 *   <li>Carries transient UI fields for workflow action routing that are not persisted.</li>
 * </ul>
 *
 * <p><b>Approval Workflow Lifecycle:</b></p>
 * <ol>
 *   <li>Register is created with a unique {@code budgetRegisterNumber}.</li>
 *   <li>{@link BudgetItem} records are added and associated with the register.</li>
 *   <li>Register is submitted for approval through the workflow engine.</li>
 *   <li>Status advances as it moves through the approval hierarchy.</li>
 *   <li>Register is finally approved or rejected by authorised personnel.</li>
 * </ol>
 *
 * <p><b>Key Relationships:</b></p>
 * <ul>
 *   <li>One {@code BudgetRegister} contains many {@link BudgetItem} records (one-to-many,
 *       cascade all, orphan removal enabled).</li>
 *   <li>Extends {@link StateAware} for workflow state management.</li>
 * </ul>
 *
 * <p>Mapped to the database table {@code EG_BUDGETREGISTER}, with primary keys generated
 * from the sequence {@code SEQ_EG_BUDGETREGISTER}.</p>
 *
 * @see BudgetItem
 * @see StateAware
 * @see CFinancialYear
 * @see EgwStatus
 */


@Entity
@Table(name = "EG_BUDGETREGISTER")
@SequenceGenerator(
        name = BudgetRegister.SEQ_EG_BUDGETREGISTER,
        sequenceName = BudgetRegister.SEQ_EG_BUDGETREGISTER,
        allocationSize = 1
)
@Getter
@Setter
public class BudgetRegister extends StateAware implements java.io.Serializable {

    public static final String SEQ_EG_BUDGETREGISTER = "SEQ_EG_BUDGETREGISTER";
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = SEQ_EG_BUDGETREGISTER, strategy = GenerationType.SEQUENCE)
    private Long id;

    @SafeHtml
    @Length(max = 50)
    @Column(unique = true, updatable = false, name = "budgetregisternumber")
    private String budgetRegisterNumber;

    @SafeHtml
    @Length(max = 100)
    @Column(updatable = false, name = "budgetregistername")
    private String budgetRegisterName;

    @NotNull
    private Date createdDate = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currentfinancialyearid", nullable = false)
    private CFinancialYear currentFinancialYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financialyearid", nullable = false)
    private CFinancialYear financialYear;


    @ManyToOne
    @JoinColumn(name = "statusid")
    private EgwStatus status;

//    @Column(name = "state_type", length = 100)
//    private String stateType = "BUDGET_APPROVAL";


    /** Relationship to existing BudgetItem entries */
    @OneToMany(mappedBy = "budgetRegister", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BudgetItem> budgetItems = new LinkedHashSet<>();

    // --- Transient UI fields ---
    @Transient
    @SafeHtml
    private String approvalComent;
    @Transient
    @SafeHtml
    private String approvalDesignation;
    @Transient
    @SafeHtml
    private String approvalDepartment;
    @Transient
    @SafeHtml
    private String workFlowAction;


    @Transient
    private User createdByUser;


    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getStateDetails() {
        return getState().getComments().isEmpty() ? budgetRegisterNumber : budgetRegisterNumber + "-" + getState().getComments();
    }


}
