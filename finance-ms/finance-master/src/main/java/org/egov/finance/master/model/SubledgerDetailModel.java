package org.egov.finance.master.model;

import org.egov.finance.master.entity.SubledgerDetail;

import lombok.Builder;
import lombok.Data;

@Data
public class SubledgerDetailModel {
	private Long id;
	private Long accountDetailTypeId;
	private Long accountDetailKeyId;
	private Double amount;
	
	public SubledgerDetailModel(SubledgerDetail sub) {
	    this.id = sub.getId();
	    this.accountDetailTypeId = sub.getAccountDetailType() != null ? sub.getAccountDetailType().getId() : null;
	    this.accountDetailKeyId = sub.getAccountDetailKey() != null ? sub.getAccountDetailKey().getId() : null;
	    this.amount = sub.getAmount();
	}

}
