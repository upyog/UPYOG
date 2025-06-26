package org.egov.finance.voucher.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "chartofaccountdetail")
@SequenceGenerator(name = CChartOfAccountDetail.SEQ_CHARTOFACCOUNTDETAIL, sequenceName = CChartOfAccountDetail.SEQ_CHARTOFACCOUNTDETAIL, allocationSize = 1)
@Data
public class CChartOfAccountDetail extends AuditDetailswithVersion {

	private static final long serialVersionUID = 1L;

	public static final String SEQ_CHARTOFACCOUNTDETAIL = "SEQ_CHARTOFACCOUNTDETAIL";

	@Id
	@GeneratedValue(generator = SEQ_CHARTOFACCOUNTDETAIL, strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "glcodeid", nullable = false)
	private CChartOfAccounts glCodeId;

	@ManyToOne
	@JoinColumn(name = "detailtypeid", nullable = false)
	private AccountDetailType detailTypeId;
}
