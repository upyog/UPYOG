package org.egov.finance.master.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "subledger_detail")
@SequenceGenerator(name = SubledgerDetail.SEQ, sequenceName = SubledgerDetail.SEQ, allocationSize = 1)
@Data
public class SubledgerDetail extends AuditDetailswithVersion {

	public static final String SEQ = "seq_subledger_detail";

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_detail_id")
	private AccountDetail accountDetail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_detail_type_id")
	private AccountDetailType accountDetailType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_detail_key_id")
	private AccountDetailKey accountDetailKey;

	private Double amount;
}
