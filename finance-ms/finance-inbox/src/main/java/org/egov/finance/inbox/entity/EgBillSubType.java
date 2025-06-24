
package org.egov.finance.inbox.entity;

import org.egov.finance.inbox.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "EG_BILL_SUBTYPE")
@SequenceGenerator(name = EgBillSubType.SEQ_EG_BILL_SUBTYPE, sequenceName = EgBillSubType.SEQ_EG_BILL_SUBTYPE, allocationSize = 1)
public class EgBillSubType extends AbstractPersistable<Long> implements java.io.Serializable {

    private static final long serialVersionUID = 1350774346491188471L;

    public static final String SEQ_EG_BILL_SUBTYPE = "SEQ_EG_BILL_SUBTYPE";

    @Id
    @GeneratedValue(generator = SEQ_EG_BILL_SUBTYPE, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Length(max = 120)
    @SafeHtml
    @NotNull
    private String name;

    @Length(max = 50)
    @SafeHtml
    @Column(name = "expenditure_type")
    @NotNull
    private String expenditureType;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getExpenditureType() {
        return expenditureType;
    }

    public void setExpenditureType(final String expenditureType) {
        this.expenditureType = expenditureType;
    }
}
