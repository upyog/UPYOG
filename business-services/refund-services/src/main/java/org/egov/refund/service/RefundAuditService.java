package org.egov.refund.service;

import org.egov.refund.model.Refund;

public interface RefundAuditService {

	 void createAudit(Refund refund, String action);
}