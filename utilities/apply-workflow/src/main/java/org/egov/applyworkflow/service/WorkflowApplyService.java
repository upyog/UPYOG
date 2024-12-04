package org.egov.applyworkflow.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.egov.applyworkflow.config.AppConfig;
import org.egov.applyworkflow.util.WorkflowMapper;
import org.egov.applyworkflow.util.WorkflowMapperImpl;
import org.egov.applyworkflow.util.WorkflowMergeUtility;
import org.egov.applyworkflow.web.model.BusinessService;
import org.egov.applyworkflow.web.model.WABusinessServiceRequest;
import org.egov.applyworkflow.web.model.WorkflowApplyRequest;
import org.egov.applyworkflow.web.model.workflow.BusinessServiceRequest;
import org.egov.applyworkflow.repository.ServiceRequestRepository;
import org.egov.applyworkflow.web.model.workflow.BusinessServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    private final ObjectMapper objectMapper;
    private Object response;
    private final WorkflowMergeUtility workflowMergeUtility;

    private final WorkflowMapper workflowMapper = new WorkflowMapperImpl();

    public WorkflowApplyService(ServiceRequestRepository serviceRequestRepository, ObjectMapper objectMapper, WorkflowMergeUtility workflowMergeUtility) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.objectMapper = objectMapper;
        this.workflowMergeUtility = workflowMergeUtility;
        ;
    }

    /**
     * Processes the workflow request based on the apply type (CREATE or UPDATE).
     *
     * @param payload the payload containing the workflow details.
     * @return a {@link BusinessServiceResponse} containing the result of the workflow operation.
     * @throws IllegalArgumentException if the apply type is invalid.
     */
    public BusinessServiceResponse processWorkflow(WorkflowApplyRequest payload) {
        // Determine the operation type (CREATE or UPDATE) based on applyType
        String applyType = payload.getBusinessServiceDto().getApplyType();
        switch (applyType.toUpperCase()) {
            case WORKFLOW_CREATE:
                return handleCreateWorkflow(payload);
            case WORKFLOW_UPDATE:
                return handleUpdateWorkflow(payload);
            default:
                throw new IllegalArgumentException("Invalid applyType: " + applyType + ". Allowed values are " + WORKFLOW_CREATE + " or " + WORKFLOW_UPDATE + ".");
        }
    }


    /**
     * Handles the creation of a new workflow.
     *
     * @param payload the payload containing the workflow details.
     * @return a {@link BusinessServiceResponse} containing the result of the create operation.
     */
    private BusinessServiceResponse handleCreateWorkflow(WorkflowApplyRequest payload) {
        // Fetch business service data from MDMS or other source
        BusinessService businessService = egMdmsDataService.getData(
                payload.getBusinessServiceDto().getTenantId(),
                payload.getBusinessServiceDto().getUniqueIdentifier()
        );

        // Prepare WorkflowRequest
        WABusinessServiceRequest workflowRequest = new WABusinessServiceRequest();
        workflowRequest.setRequestInfo(payload.getRequestInfo());
        workflowRequest.addBusinessServiceItem(businessService);

        // Construct Search URL
        StringBuilder searchUrl = new StringBuilder(appConfig.getWfContextPath());
        searchUrl.append(appConfig.getSearchPath());
        String searchParam = String.format("?businessServices=%s&tenantId=%s",
                //appConfig.getSearchPath(),
                workflowRequest.getBusinessServices().get(0).getBusinessService(),
                workflowRequest.getBusinessServices().get(0).getTenantId()

        );
        searchUrl.append(searchParam);
        log.info("Fetching existing workflow for update at URI: {}", searchUrl);

        // Fetch existing workflow
        Object response = serviceRequestRepository.fetchResult(searchUrl, workflowRequest);
        BusinessServiceResponse businessServiceResponse = objectMapper.convertValue(response, BusinessServiceResponse.class);

        // check if workflow exits or not
        if (Optional.ofNullable(businessServiceResponse)
                .map(BusinessServiceResponse::getBusinessServices)
                .filter(services -> !services.isEmpty())
                .isPresent()) {
            throw new IllegalStateException("Workflow exists, so it cannot be created but can be updated.");
        }


        // Construct Create URL
        StringBuilder url = new StringBuilder(appConfig.getWfContextPath());
        url.append(appConfig.getCreatePath());
        log.info("Creating workflow at URI: {}", url);
        log.info("Payload: {}", workflowRequest);

        // Make API call for creation
        response = serviceRequestRepository.fetchResult(url, workflowRequest);
        return objectMapper.convertValue(response, BusinessServiceResponse.class);
    }


    /**
     * Handles the update of an existing workflow.
     *
     * @param payload the payload containing the workflow details.
     * @return a {@link BusinessServiceResponse} containing the result of the update operation.
     * @throws IllegalStateException if no existing workflow is found for the update operation.
     */
    private BusinessServiceResponse handleUpdateWorkflow(WorkflowApplyRequest payload) {
        // Fetch business service data from MDMS or other source
        BusinessService businessService = egMdmsDataService.getData(
                payload.getBusinessServiceDto().getTenantId(),
                payload.getBusinessServiceDto().getUniqueIdentifier()
        );

        // Prepare WorkflowRequest
        WABusinessServiceRequest workflowRequest = new WABusinessServiceRequest();
        workflowRequest.setRequestInfo(payload.getRequestInfo());
        workflowRequest.addBusinessServiceItem(businessService);

        // Construct Search URL
        StringBuilder searchUrl = new StringBuilder(appConfig.getWfContextPath());
        searchUrl.append(appConfig.getSearchPath());
        String searchParam = String.format("?businessServices=%s&tenantId=%s",
                //appConfig.getSearchPath(),
                workflowRequest.getBusinessServices().get(0).getBusinessService(),
                workflowRequest.getBusinessServices().get(0).getTenantId()

        );
        searchUrl.append(searchParam);
        log.info("Fetching existing workflow for update at URI: {}", searchUrl);

        // Fetch existing workflow
        Object response = serviceRequestRepository.fetchResult(searchUrl, workflowRequest);
        BusinessServiceResponse businessServiceResponse = objectMapper.convertValue(response, BusinessServiceResponse.class);

        if (businessServiceResponse == null || businessServiceResponse.getBusinessServices().isEmpty()) {
            throw new IllegalStateException("No existing workflow found for update.");
        }

        // Prepare updated WorkflowRequest
        BusinessServiceRequest workflowResponse = new BusinessServiceRequest();
        workflowResponse.setRequestInfo(workflowRequest.getRequestInfo());
        workflowResponse.setBusinessServices(businessServiceResponse.getBusinessServices());

        // Update business service using WorkflowMapper
        BusinessServiceRequest updatedRequest = updateBusinessServices(workflowRequest, workflowResponse);

        // Rebuild WorkflowRequest for Update
        // workflowResponse.setBusinessServices(Collections.singletonList(updatedBusinessService));

        // Construct Update URL
        StringBuilder updateUrl = new StringBuilder(appConfig.getWfContextPath());
        updateUrl.append(appConfig.getUpdatePath());
        log.info("Updating workflow at URI: {}", updateUrl);
        log.info("Updated Payload: {}", workflowResponse);

        // Make API call for update
        Object updateResponse = serviceRequestRepository.fetchResult(updateUrl, workflowResponse);
        return objectMapper.convertValue(updateResponse, BusinessServiceResponse.class);
    }


    /**
     * Updates the 0th BusinessService object in the target request with values from the source request.
     *
     * @param sourceRequest Source BusinessServiceRequest (containing org.egov.applyworkflow.web.model.BusinessService)
     * @param targetRequest Target BusinessServiceRequest (containing org.egov.applyworkflow.web.model.workflow.BusinessService)
     * @return Updated target BusinessServiceRequest object
     */
    public BusinessServiceRequest updateBusinessServices(
            WABusinessServiceRequest sourceRequest,
            BusinessServiceRequest targetRequest) {

        if (sourceRequest.getBusinessServices() == null || sourceRequest.getBusinessServices().isEmpty()) {
            throw new IllegalArgumentException("Source BusinessServiceRequest contains no BusinessServices.");
        }

        if (targetRequest.getBusinessServices() == null || targetRequest.getBusinessServices().isEmpty()) {
            throw new IllegalArgumentException("Target BusinessServiceRequest contains no BusinessServices.");
        }

        // Get the 0th BusinessService objects
        org.egov.applyworkflow.web.model.BusinessService source = sourceRequest.getBusinessServices().get(0);
        org.egov.applyworkflow.web.model.workflow.BusinessService target = targetRequest.getBusinessServices().get(0);

        // Merge source into target
        workflowMapper.updateBusinessService(source, target);

        // Handle nested States and Actions for the 0th BusinessService
        if (source.getStates() != null && target.getStates() != null) {
            for (int i = 0; i < source.getStates().size(); i++) {
                workflowMapper.updateState(source.getStates().get(i), target.getStates().get(i));

                // Handle nested Actions
                if (source.getStates().get(i).getActions() != null && target.getStates().get(i).getActions() != null) {
                    for (int j = 0; j < source.getStates().get(i).getActions().size(); j++) {
                        workflowMapper.updateAction(
                                source.getStates().get(i).getActions().get(j),
                                target.getStates().get(i).getActions().get(j)
                        );
                    }
                }
            }
        }

        // Return the updated target request
        return targetRequest;
    }
}