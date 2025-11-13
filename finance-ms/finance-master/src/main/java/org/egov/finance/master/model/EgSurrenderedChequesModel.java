package org.egov.finance.master.model;

import java.util.Date;

import org.egov.finance.master.customannotation.SafeHtml;

import lombok.Data;

@Data
public class EgSurrenderedChequesModel {
	private Long id;

	private Long bankaccountId;

	@SafeHtml
	private String chequenumber;

	private Date chequedate;

	private Long voucherheaderId;

	private Date lastmodifieddate;

}
