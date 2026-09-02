package org.egov.refund.service;

import org.egov.refund.model.Refund;
import org.egov.refund.model.WorkflowTransition;
import org.egov.refund.web.contracat.RefundActionRequest;

public interface WorkflowService {

    WorkflowTransition validateAndGetNextState(
            Refund refund,
            RefundActionRequest request
    );
}