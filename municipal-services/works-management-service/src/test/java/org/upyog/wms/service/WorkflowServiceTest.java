package org.upyog.wms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.Test;
import org.upyog.wms.constants.ActionPlanAction;
import org.upyog.wms.web.models.ActionPlan;

/** Workflow switched off: the local state machine publishes on submission. */
class WorkflowServiceTest {

    private final WorkflowService service = new WorkflowService(null, null);
    private final ActionPlan plan = ActionPlan.builder().applicationNumber("WMS-AP-1").build();

    @Test
    void localTransitions() {
        assertEquals("INITIATED", service.transition(null, plan, ActionPlanAction.INITIATE, null, false));
        assertEquals("APPROVED", service.transition(null, plan, ActionPlanAction.SUBMIT, "INITIATED", false));
        assertEquals("APPROVED", service.transition(null, plan, ActionPlanAction.RESUBMIT, "RETURNED", false));
        assertEquals("WITHDRAWN", service.transition(null, plan, ActionPlanAction.WITHDRAW, "INITIATED", false));
    }

    @Test
    void invalidLocalTransitionIsRejected() {
        assertThrows(CustomException.class,
                () -> service.transition(null, plan, ActionPlanAction.APPROVE, "INITIATED", false));
        assertThrows(CustomException.class,
                () -> service.transition(null, plan, ActionPlanAction.SUBMIT, "APPROVED", false));
    }
}
