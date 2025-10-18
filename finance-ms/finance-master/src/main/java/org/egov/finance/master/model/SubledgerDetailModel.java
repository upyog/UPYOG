package org.egov.finance.master.model;

import org.egov.finance.master.entity.CGeneralLedgerDetail;
import org.egov.finance.master.entity.SubledgerDetail;

import lombok.Builder;
import lombok.Data;

@Data
public class SubledgerDetailModel {
	private Long id;
	private Long accountDetailTypeId;
	private Long accountDetailKeyId;
	private Double amount;

	public SubledgerDetailModel(final CGeneralLedgerDetail sub) {
		this.id = sub.getId();
		this.amount = sub.getAmount() != null ? sub.getAmount().doubleValue() : null;

		if (sub.getDetailTypeId() != null) {
			this.accountDetailTypeId = sub.getDetailTypeId().getId();
		}

		if (sub.getDetailKeyId() != null) {
			this.accountDetailKeyId = sub.getDetailKeyId().longValue();
		}
	}
}
