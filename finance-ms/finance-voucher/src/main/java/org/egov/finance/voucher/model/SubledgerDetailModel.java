package org.egov.finance.voucher.model;

import org.egov.finance.voucher.entity.CGeneralLedgerDetail;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubledgerDetailModel {

	private Long id;
	private Integer detailKeyId;
	private String detailKeyName;
	private Double amount;
	private Long accountDetailTypeId;

	public SubledgerDetailModel(CGeneralLedgerDetail detail) {
		this.id = detail.getId();
		this.detailKeyId = detail.getDetailKeyId();
		this.detailKeyName = detail.getDetailKeyName();
		this.amount = detail.getAmount() != null ? detail.getAmount().doubleValue() : null;
		this.accountDetailTypeId = detail.getDetailTypeId() != null ? detail.getDetailTypeId().getId() : null;
	}
}
