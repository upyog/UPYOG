package org.egov.finance.master.entity;

import java.util.Date;

import org.egov.finance.master.customannotation.SafeHtml;

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
import lombok.Data;

@Entity
@Table(name = "fiscalperiod", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
@SequenceGenerator(name = FiscalPeriod.SEQ, sequenceName = FiscalPeriod.SEQ, allocationSize = 1)
@Data
public class FiscalPeriod extends AuditDetailswithVersion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SEQ = "seq_fiscalperiod";

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	@SafeHtml
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "financialyearid")
	private FinancialYear financialYear;

	private Date startingDate;
	private Date endingDate;

	private Boolean active;
	private Boolean isActiveForPosting;
	private Boolean isClosed;
}
