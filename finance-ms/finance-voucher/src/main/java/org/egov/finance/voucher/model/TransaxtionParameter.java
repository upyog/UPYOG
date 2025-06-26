package org.egov.finance.voucher.model;

import lombok.Data;

@Data
public class TransaxtionParameter {

	/**
	 * name of the detail key
	 */
	protected String detailName;

	/**
	 * value of the detail key
	 */
	protected String detailKey;

	protected String detailAmt;

	protected String glcodeId;

	protected String tdsId;

	protected String detailTypeId;

}
