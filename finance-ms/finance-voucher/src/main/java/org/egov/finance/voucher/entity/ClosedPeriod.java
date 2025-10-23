package org.egov.finance.voucher.entity;

import java.util.Date;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.egov.finance.voucher.enumeration.CloseTypeEnum;
import org.springframework.data.jpa.domain.AbstractAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "closedperiods")
@SequenceGenerator(name = ClosedPeriod.SEQ, sequenceName = ClosedPeriod.SEQ, allocationSize = 1)
@Data
public class ClosedPeriod extends AuditDetailswithVersion {

	private static final long serialVersionUID = 1L;
	public static final String SEQ = "seq_closedperiods";

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id = null;

	private Date startingDate;
	@Transient
	private int fromDate;
	@Transient
	private int toDate;

	private Date endingDate;

	private Boolean isClosed = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "closetype")
	private CloseTypeEnum closeType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "financialYearId", nullable = false)
	private FinancialYear financialYear;

	@SafeHtml
	@NotNull
	private String remarks;

}
