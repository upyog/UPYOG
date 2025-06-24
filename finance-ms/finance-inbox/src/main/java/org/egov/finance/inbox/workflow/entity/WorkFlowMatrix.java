

package org.egov.finance.inbox.workflow.entity;

import static org.egov.finance.inbox.workflow.entity.WorkFlowMatrix.SEQ_WF_MATRIX;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.inbox.customannotation.SafeHtml;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EG_WF_MATRIX")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = SEQ_WF_MATRIX, sequenceName = SEQ_WF_MATRIX, allocationSize = 1)
public class WorkFlowMatrix  implements Cloneable,Serializable {

    public static final String SEQ_WF_MATRIX = "SEQ_EG_WF_MATRIX";
    private static final long serialVersionUID = 4954386159285858993L;
    @Id
    @GeneratedValue(generator = SEQ_WF_MATRIX, strategy = GenerationType.SEQUENCE)
    private Long id;

    @SafeHtml
    private String department;

    @NotNull
    @SafeHtml
    private String objectType;

    @SafeHtml
    private String currentState;

    @SafeHtml
    private String currentStatus;

    @SafeHtml
    private String pendingActions;

    @SafeHtml
    private String currentDesignation;

    @SafeHtml
    private String additionalRule;

    @SafeHtml
    private String nextState;

    @SafeHtml
    private String nextAction;

    @SafeHtml
    private String nextDesignation;

    @SafeHtml
    private String nextStatus;

    @SafeHtml
    private String validActions;

    private BigDecimal fromQty;

    private BigDecimal toQty;

    @Temporal(TemporalType.DATE)
    private Date fromDate;

    @Temporal(TemporalType.DATE)
    private Date toDate;

    private String enableFields;

    private Boolean forwardEnabled;

    private Long nextref;

    private Boolean rejectEnabled;

    public WorkFlowMatrix(final String department, final String objectType, final String currentState,
                          final String currentStatus, final String pendingActions, final String currentDesignation,
                          final String additionalRule, final String nextState, final String nextAction, final String nextDesignation,
                          final String nextStatus, final String validActions, final BigDecimal fromQty, final BigDecimal toQty,
                          final Date fromDate, final Date toDate) {
        super();
        this.department = department;
        this.objectType = objectType;
        this.currentState = currentState;
        this.currentStatus = currentStatus;
        this.pendingActions = pendingActions;
        this.currentDesignation = currentDesignation;
        this.additionalRule = additionalRule;
        this.nextState = nextState;
        this.nextAction = nextAction;
        this.nextDesignation = nextDesignation;
        this.nextStatus = nextStatus;
        this.validActions = validActions;
        this.fromQty = fromQty;
        this.toQty = toQty;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

   

    @Override
    public WorkFlowMatrix clone() {
        return new WorkFlowMatrix(department, objectType, currentState, currentStatus, pendingActions,
                currentDesignation, additionalRule, nextState, nextAction, nextDesignation, nextStatus, validActions,
                fromQty, toQty, fromDate, toDate);
    }

  
}