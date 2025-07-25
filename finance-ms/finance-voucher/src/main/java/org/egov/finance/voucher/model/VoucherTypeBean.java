package org.egov.finance.voucher.model;

import java.util.Date;

import lombok.Data;

@Data
public class VoucherTypeBean {

	private String voucherName;
	private String voucherType;
	private String voucherNumType;
	private String cgnType;
	private String voucherNumFrom;
	private String voucherNumTo;
	private String voucherDateFrom;
	private String voucherSubType;
	private String totalAmount;
	/**
	 * @description - added properties for the contractor,supplier,salary and fixed
	 *              asset JV manual creation screen.
	 *
	 */

	private String partyBillNum;
	private String partyName;
	private Date partyBillDate;
	private String billNum;
	private Date billDate;

}
