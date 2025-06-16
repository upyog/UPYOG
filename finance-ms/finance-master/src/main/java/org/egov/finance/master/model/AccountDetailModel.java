package org.egov.finance.master.model;

import java.util.ArrayList;
import java.util.List;

import org.egov.finance.master.customannotation.SafeHtml;
import org.egov.finance.master.entity.AccountDetail;
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

	// Constructor to map from entity
	public AccountDetailModel(AccountDetail accountDetail) {
		this.id = accountDetail.getId();
		this.orderId = accountDetail.getOrderId();
		this.glcode = accountDetail.getGlcode();
		this.debitAmount = accountDetail.getDebitAmount();
		this.creditAmount = accountDetail.getCreditAmount();
		this.functionId = accountDetail.getFunction() != null ? accountDetail.getFunction().getId() : null;

		if (accountDetail.getSubledgerDetails() != null) {
			for (SubledgerDetail sub : accountDetail.getSubledgerDetails()) {
				this.subledgerDetails.add(new SubledgerDetailModel(sub));
			}
		}
	}

}
