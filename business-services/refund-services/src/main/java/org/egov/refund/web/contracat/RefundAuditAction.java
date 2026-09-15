package org.egov.refund.web.contracat;

public final class RefundAuditAction {

    private RefundAuditAction() {
    }

    public static final String CREATE = "CREATE";
    public static final String UPDATE = "UPDATE";
    public static final String VERIFY = "VERIFY";
    public static final String APPROVE = "APPROVE";
    public static final String REJECT = "REJECT";
    public static final String SUBMIT_TO_FINANCE = "SUBMIT_TO_FINANCE";
    public static final String FINANCE_APPROVED = "FINANCE_APPROVED";
    public static final String FINANCE_REJECTED = "FINANCE_REJECTED";
    public static final String REFUND_PROCESSING = "REFUND_PROCESSING";
    public static final String REFUND_COMPLETED = "REFUND_COMPLETED";
    public static final String FAILED = "FAILED";
    public static final String RETRY = "RETRY";
    public static final String CANCEL = "CANCEL";
}