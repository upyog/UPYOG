package org.egov.finance.master.model;

import java.util.ArrayList;
import java.util.List;

import org.egov.finance.master.customannotation.SafeHtml;
import org.egov.finance.master.entity.AccountDetail;
import org.egov.finance.master.entity.CGeneralLedger;
import org.egov.finance.master.entity.CGeneralLedgerDetail;
import org.egov.finance.master.entity.SubledgerDetail;

import lombok.Data;

@Data
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
		this.creditAmount = gl.getCreditAmount();
		this.debitAmount = gl.getDebitAmount();
		this.functionId = gl.getFunctionId() != null ? gl.getFunctionId().longValue() : null;

		if (gl.getGeneralLedgerDetails() != null) {
			for (final CGeneralLedgerDetail sub : gl.getGeneralLedgerDetails()) {
				this.subledgerDetails.add(new SubledgerDetailModel(sub));
			}
		}
	}

}
