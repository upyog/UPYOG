package org.egov.finance.master.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "financialyear", uniqueConstraints = @UniqueConstraint(columnNames = { "financialyear", "id" }))
@SequenceGenerator(name = FinancialYear.SEQ, sequenceName = FinancialYear.SEQ, allocationSize = 1)
@Data
public class FinancialYear extends AuditDetailswithVersion {

	public static final String SEQ = "SEQ_FINANCIAL_YEAR";

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	private String finYearRange;

	private Date startingDate;

	private Date endingDate;

	private Boolean active;

	private Boolean isActiveForPosting;

	private Boolean isClosed;
}
