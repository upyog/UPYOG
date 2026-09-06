package org.egov.refund.util;

public final class RefundConstants {

	private RefundConstants() {
	}

	public static final String PAYMENT_MODE_ONLINE = "ONLINE";
	public static final String REFUND_MODE_OFFLINE = "OFFLINE";

	public static final String PAYMENT_MODE_CASH = "CASH";
	
	public static final String PAYMENT_MODE_DD = "DD";
	
	public static final String PAYMENT_MODE_CHEQUE = "CHEQUE";

	public static final String SUCCESS_GATEWAY_CODE = "OTS0000";

	public static final String STATUS_CREATED = "CREATED";

	public static final String STATUS_INITIATE = "INITIATE";

	public static final String ACTION_SUBMITTED = "SUBMITTED";

	public static final String ACTION_FINANCE_PENDING = "PENDING_WITH_FINANCE";

	public static final String SYSTEM_USER = "SYSTEM";

	public static final String ACTION_APPROVE = "APPROVE";
	public static final String ACTION_CREATE_REQUEST = "CREATE_REQUEST";
	public static final String ACTION_REFUND_INITIATE = "REFUND_INITIATE";

	public static final String STATUS_PENDING_WITH_FINANCE = "PENDING_WITH_FINANCE";

	public static final String STATUS_REFUND_REJECTED = "REFUND_REJECTED";

	public static final String ACTION_REJECT = "REJECT";

}