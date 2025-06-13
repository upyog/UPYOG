package org.egov.finance.master.model;

import lombok.Data;

@Data
public class SubledgerDetailModel {
	private Long id;
	private Long accountDetailTypeId;
	private Long accountDetailKeyId;
	private Double amount;

}
