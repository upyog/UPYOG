package org.egov.finance.voucher.model;

import lombok.Data;

@Data
public class GLParameter {

	/**
	*
	*/
	private static final long serialVersionUID = 2875426573880846321L;
	/**
	 * if this detail is required for posting?
	 */

	private int glcodeId;
	/**
	 * Name of the detail the GL code requires
	 */
	private Long detailId;

	private String detailName;

	/**
	 * The value of the detail for that detail name
	 */
	private String detailKey;

	private String detailAmt;

	private String tdsId;

}
