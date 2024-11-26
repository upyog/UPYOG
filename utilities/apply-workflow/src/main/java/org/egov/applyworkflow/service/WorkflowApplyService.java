package org.egov.applyworkflow.service;


import lombok.extern.slf4j.Slf4j;
import org.egov.applyworkflow.config.AppConfig;
import org.egov.applyworkflow.model.BusinessService;
import org.egov.applyworkflow.model.WorkflowApplyRequest;
import org.egov.applyworkflow.model.WorkflowRequest;
import org.egov.applyworkflow.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.applyworkflow.util.Constants.WORKFLOW_CREATE;
import static org.egov.applyworkflow.util.Constants.WORKFLOW_UPDATE;

@Slf4j
@Service
public class WorkflowApplyService {

    @Autowired
    private final ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private EgMdmsDataService egMdmsDataService;

    public WorkflowApplyService(ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
    }

    public String processWorkflow(WorkflowApplyRequest payload) {
        // Fetch business service data
        BusinessService businessService = egMdmsDataService.getData(
                payload.getBusinessServiceDto().getTenantId(),
                payload.getBusinessServiceDto().getUniqueIdentifier()
        );

        // Prepare WorkflowRequest

        WorkflowRequest workflowRequest = new WorkflowRequest();
        workflowRequest.setRequestInfo(payload.getRequestInfo());
        workflowRequest.getBusinessServices().add(businessService);

        // Determine the operation type (Create or Update) based on applyType
        String applyType = payload.getBusinessServiceDto().getApplyType(); // Fetch the apply type
        StringBuilder url = new StringBuilder(appConfig.getWfContextPath());

        if (WORKFLOW_CREATE.equalsIgnoreCase(applyType)) {
            url.append(appConfig.getCreatePath());
        } else if (WORKFLOW_UPDATE.equalsIgnoreCase(applyType)) {
            url.append(appConfig.getUpdatePath());
        } else {
            throw new IllegalArgumentException("Invalid applyType: " + applyType + ". Allowed values are "+WORKFLOW_CREATE+" or "+WORKFLOW_UPDATE+".");
        }

        log.info("Processing operation for workflow with URI: {}", url);
        log.info("Payload is : {}", workflowRequest.toString());
        // Make the service call
        Object response = serviceRequestRepository.fetchResult(url, workflowRequest);
        return response.toString();
    }
}