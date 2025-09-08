package org.egov.finance.voucher.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Transaxtion {

	/**
	 * gl Code whose transaction is to be recorded
	 */
	protected String glCode;

	/**
	 * Name of the gl Code
	 */
	protected String glName;

	/**
	 * Amount to be debited from the glCode
	 */
	protected String drAmount;

	/**
	 * Amount to be credited for this GL Code
	 */
	protected String crAmount;

	/**
	 * Collection of Transaxtion class
	 */

	protected String voucherLineId;
	protected String voucherDetailId;

	protected String voucherHeaderId;

	protected String narration = "";

	private List<TransaxtionParameter> transaxtionParameters = new ArrayList<>();

	protected String functionId;

	protected String functionaryId;
	protected String schemeId;
	protected String subSchemeId;
	protected String field;
	protected String asOnDate;
	protected Long billId;

}
