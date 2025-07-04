package org.egov.finance.report.model;

import java.util.Date;

import org.egov.finance.report.customannotation.SafeHtml;

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
