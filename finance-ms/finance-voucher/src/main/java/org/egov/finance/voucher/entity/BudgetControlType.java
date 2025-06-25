
package org.egov.finance.voucher.entity;

import org.egov.finance.voucher.customannotation.SafeHtml;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "EGF_BudgetControlType")
@SequenceGenerator(name = BudgetControlType.SEQ, sequenceName = BudgetControlType.SEQ, allocationSize = 1)
public class BudgetControlType extends AuditDetailswithVersion {

    private static final long serialVersionUID = -1663676230513314512L;
    public static final String SEQ = "seq_EGF_BudgetControlType";

    public enum BudgetCheckOption {
        NONE, ANTICIPATORY, MANDATORY
    }

    @Id
    @GeneratedValue(generator = BudgetControlType.SEQ, strategy = GenerationType.SEQUENCE)
    private Long id;
    // @Audited
    @SafeHtml
    private String value;

   

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

}