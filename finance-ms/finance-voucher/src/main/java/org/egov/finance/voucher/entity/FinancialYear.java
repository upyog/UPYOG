package org.egov.finance.voucher.entity;

import java.util.Date;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "financialyear", uniqueConstraints = @UniqueConstraint(columnNames = { "financialyear", "id" }))
@SequenceGenerator(name = FinancialYear.SEQ, sequenceName = FinancialYear.SEQ, allocationSize = 1)
@Data
public class FinancialYear extends AuditDetailswithVersion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SEQ = "SEQ_FINANCIAL_YEAR";

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	@Length(min = 1, max = 25)
    @NotNull
    @SafeHtml
    @Column(name="financialyear")
    private String finYearRange;

	private Date startingDate;

	private Date endingDate;

	@Column(name = "isactive")
	private Boolean active;

	private Boolean isActiveForPosting;

	private Boolean isClosed;
}
