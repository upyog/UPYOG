package org.egov.finance.inbox.entity;

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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	public SubledgerDetail(final CGeneralLedgerDetail sub) {
		this.id = sub.getId();
		this.amount = sub.getAmount() != null ? sub.getAmount().doubleValue() : null;

		if (sub.getDetailTypeId() != null) {
			this.accountDetailType = new AccountDetailType(sub.getDetailTypeId().getId());
		}

		if (sub.getDetailKeyId() != null) {
			this.accountDetailKey = new AccountDetailKey(sub.getDetailKeyId().longValue());
		}

		this.amount = sub.getAmount().doubleValue();
	}

}
