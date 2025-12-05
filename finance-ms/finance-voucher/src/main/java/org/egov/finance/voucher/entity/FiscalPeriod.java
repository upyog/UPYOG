package org.egov.finance.voucher.entity;

import java.util.Date;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "fiscalperiod", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
@SequenceGenerator(name = FiscalPeriod.SEQ_CFISCALPERIOD, sequenceName = FiscalPeriod.SEQ_CFISCALPERIOD, allocationSize = 1)
@Data
public class FiscalPeriod extends AuditDetailswithVersion {

	private static final long serialVersionUID = -5166451072153556422L;

	public static final String SEQ_CFISCALPERIOD = "SEQ_FISCALPERIOD";

	@Id
	@GeneratedValue(generator = SEQ_CFISCALPERIOD, strategy = GenerationType.SEQUENCE)
	private Long id;

	private Integer type = 0;

	@Length(min = 1, max = 25)
	@NotNull
	@SafeHtml
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "financialyearid", updatable = false)
	private FinancialYear cFinancialYear;

	private Integer parentId = 0;

	@NotNull
	@DateTimeFormat
	private Date startingDate;

	@NotNull
	@DateTimeFormat
	private Date endingDate;

	@Column(name = "isactive")
	private Boolean isActive;

	private Boolean isActiveForPosting;

	private Boolean isClosed;

}