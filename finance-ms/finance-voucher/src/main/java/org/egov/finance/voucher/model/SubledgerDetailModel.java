package org.egov.finance.voucher.model;

import org.egov.finance.voucher.entity.CGeneralLedgerDetail;
import org.egov.finance.voucher.entity.SubledgerDetail;

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
