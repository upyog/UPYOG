package org.egov.refund.service;

import org.egov.refund.model.Refund;
import org.egov.refund.web.contracat.RefundActionRequest;
import org.egov.refund.web.contracat.RefundRequest;

public interface RefundEnrichmentService {

	void enrichRefundPostValidate(RefundRequest request);

	void enrichRefundUpdate(Refund existingRefund, Refund inputRefund);

	void updateAuditDetails(Refund refund, String userId, long currentTime);

	RefundActionRequest enrichWorkflowAction(RefundRequest request, String action);
}