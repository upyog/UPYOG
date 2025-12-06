package org.egov.applyworkflow.util;

import org.egov.applyworkflow.web.model.BusinessService;
import org.egov.applyworkflow.web.model.State;
import org.egov.applyworkflow.web.model.Action;

public class WorkflowMapperImpl implements WorkflowMapper {

    @Override
    public void updateBusinessService(org.egov.applyworkflow.web.model.BusinessService source, org.egov.applyworkflow.web.model.workflow.BusinessService target) {
        if (source == null || target == null) {
            return;
        }

        target.setBusiness(source.getBusiness());
        target.setBusinessService(source.getBusinessService());
        target.setBusinessServiceSla(source.getBusinessServiceSla());
        // Fields ignored: uuid, businessServiceId, auditDetails
    }

    @Override
    public void updateState(org.egov.applyworkflow.web.model.State source, org.egov.applyworkflow.web.model.workflow.State target) {
        if (source == null || target == null) {
            return;
        }

        target.setState(source.getState());
        target.setApplicationStatus(source.getApplicationStatus());
        target.setDocUploadRequired(source.getDocUploadRequired());
        target.setIsStartState(source.getIsStartState());
        target.setIsTerminateState(source.getIsTerminateState());
        target.setIsStateUpdatable(source.getIsStateUpdatable());
        target.setSla(source.getSla());
        // Fields ignored: uuid, businessServiceId, auditDetails
    }

    @Override
    public void updateAction(org.egov.applyworkflow.web.model.Action source, org.egov.applyworkflow.web.model.workflow.Action target) {
        if (source == null || target == null) {
            return;
        }

        target.setAction(source.getAction());
        target.setNextState(source.getNextState());
        target.setRoles(source.getRoles());
        target.setActive(source.getActive());
        // Fields ignored: uuid, auditDetails
    }
}
