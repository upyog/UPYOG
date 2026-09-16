package org.egov.refund.web.contracat;

import org.egov.refund.model.RefundAudit;

public class RefundAuditRequest {

    private RefundAudit refundAudit;

    public RefundAuditRequest() {
    }

    public RefundAuditRequest(RefundAudit refundAudit) {
        this.refundAudit = refundAudit;
    }

    public RefundAudit getRefundAudit() {
        return refundAudit;
    }

    public void setRefundAudit(RefundAudit refundAudit) {
        this.refundAudit = refundAudit;
    }
}