package org.egov.finance.master.entity;

import org.springframework.data.jpa.domain.AbstractAuditable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "CHARTOFACCOUNTDETAIL")
@SequenceGenerator(name = CChartOfAccountDetail.SEQ_CHARTOFACCOUNTDETAIL, sequenceName = CChartOfAccountDetail.SEQ_CHARTOFACCOUNTDETAIL, allocationSize = 1)

@Data
public class CChartOfAccountDetail extends AuditDetailswithVersion {

	public static final String SEQ_CHARTOFACCOUNTDETAIL = "SEQ_CHARTOFACCOUNTDETAIL";

	@Id
	@GeneratedValue(generator = SEQ_CHARTOFACCOUNTDETAIL, strategy = GenerationType.SEQUENCE)
	private Long id;

	@JoinTable
	@ManyToOne
	@JoinColumn(name = "glcodeid")
	@NotNull
	private CChartOfAccounts glCodeId;

	@JoinTable
	@ManyToOne
	@JoinColumn(name = "detailtypeid")
	@NotNull
	private AccountDetailType detailTypeId;
}
