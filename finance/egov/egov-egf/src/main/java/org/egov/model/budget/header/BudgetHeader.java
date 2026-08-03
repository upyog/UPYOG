package org.egov.model.budget.header;


import lombok.*;
import org.egov.commons.CFinancialYear;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;

import javax.persistence.*;
import java.time.LocalDateTime;



/**
 * Entity representing the header and master information of a budget document.
 *
 * <p>Serves as the main container for budget metadata and workflow state,
 * linking financial year information with the approval lifecycle of a budget.</p>
 *
 * <p><b>Key Characteristics:</b></p>
 * <ul>
 *   <li>Links the current and target financial years for budget planning.</li>
 *   <li>Supports a workflow-based approval process by extending {@code StateAware}.</li>
 *   <li>Implements optimistic locking via a version field for concurrent update control.</li>
 * </ul>
 *
 * <p>Mapped to the database table {@code budget_header}.</p>
 */


@Entity
@Table(name = BudgetHeader.TABLE_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetHeader extends StateAware {

    public static final String TABLE_NAME = "budget_header";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "currentfinancialyearid", nullable = false)
    private CFinancialYear currentFinancialYear;

    @ManyToOne
    @JoinColumn(name = "financialyearid", nullable = false)
    private CFinancialYear financialYear;

    @Column(name = "name", nullable = false, length = 100)
    private String name;


    @Column(name = "version")
    private Long version = 0L;          // optimistic locking

    @Override
    public String getStateDetails() {
        return name + getState().getValue();
    }
}
