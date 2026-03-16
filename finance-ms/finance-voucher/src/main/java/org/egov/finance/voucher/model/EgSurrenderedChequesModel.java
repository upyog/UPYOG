package org.egov.finance.voucher.model;

import java.util.Date;

import org.egov.finance.voucher.customannotation.SafeHtml;

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
