package org.egov.finance.voucher.entity;

import java.io.Serializable;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "EG_BILL_SUBTYPE")
@SequenceGenerator(name = EgBillSubType.SEQ_EG_BILL_SUBTYPE, sequenceName = EgBillSubType.SEQ_EG_BILL_SUBTYPE, allocationSize = 1)
@Data
public class EgBillSubType extends AuditDetailswithVersion implements Serializable {
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

}
