package org.egov.finance.voucher.model;

import java.util.ArrayList;
import java.util.List;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.egov.finance.voucher.entity.CGeneralLedger;
import org.egov.finance.voucher.entity.CGeneralLedgerDetail;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountDetailModel {

	private Long id;
	private Long orderId;

	@SafeHtml
	private String glcode;

	private Double debitAmount;
	private Double creditAmount;

	private Long functionId;

	private List<SubledgerDetailModel> subledgerDetails = new ArrayList<>();

	public AccountDetailModel(final CGeneralLedger gl) {
		this.id = gl.getId();
		this.orderId = gl.getVoucherlineId() != null ? gl.getVoucherlineId().longValue() : null;
		this.glcode = gl.getGlcode();
		this.creditAmount = gl.getCreditAmount() != null ? gl.getCreditAmount().doubleValue() : null;
		this.debitAmount = gl.getDebitAmount() != null ? gl.getDebitAmount().doubleValue() : null;
		this.functionId = gl.getFunctionId() != null ? gl.getFunctionId().longValue() : null;

		if (gl.getGeneralLedgerDetails() != null) {
			for (final CGeneralLedgerDetail sub : gl.getGeneralLedgerDetails()) {
				this.subledgerDetails.add(new SubledgerDetailModel(sub));
			}
		}
	}
}
