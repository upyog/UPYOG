package org.egov.asset.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.models.coremodels.RequestInfoWrapper;
import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.repository.ServiceRequestRepository;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetRequest;
import org.egov.asset.web.models.workflow.*;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class WorkflowService {

    private final AssetConfiguration configs;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ObjectMapper mapper;

    public WorkflowService(AssetConfiguration configs, ServiceRequestRepository serviceRequestRepository,
                           ObjectMapper mapper) {
        this.configs = configs;
        this.serviceRequestRepository = serviceRequestRepository;
        this.mapper = mapper;
    }

    /**
     * Method to integrate with workflow
     * <p>
     * takes the Pet request as parameter constructs the work-flow request
     * <p>
     * and sets the resultant status from wf-response back to trade-license object
     */
    public State callWorkFlow(ProcessInstanceRequest workflowReq) {
        StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
        Object responseObject = serviceRequestRepository.fetchResult(url, workflowReq);
        ProcessInstanceResponse response = mapper.convertValue(responseObject, ProcessInstanceResponse.class);
        return response.getProcessInstances().get(0).getState();
    }

    /**
     * method to prepare process instance request
     * and assign status back to property
     *
     * @param assetRequest The asset request to update workflow for.
     */
    public State updateWorkflow(AssetRequest assetRequest) {
        Asset asset = assetRequest.getAsset();

        ProcessInstanceRequest workflowReq = getProcessInstanceForAsset(asset, assetRequest.getRequestInfo());
        State state = callWorkFlow(workflowReq);

        assetRequest.getAsset().setStatus(state.getApplicationStatus());
        return state;
    }

    private ProcessInstanceRequest getProcessInstanceForAsset(Asset asset, RequestInfo requestInfo) {
        ProcessInstance workflow = null != asset.getWorkflow() ? asset.getWorkflow() : new ProcessInstance();

        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBusinessId(asset.getApplicationNo());
        processInstance.setAction(workflow.getAction());
        processInstance.setModuleName(workflow.getModuleName());
        processInstance.setTenantId(asset.getTenantId());
        processInstance.setBusinessService(workflow.getBusinessService());
        processInstance.setDocuments(workflow.getDocuments());
        processInstance.setComment(workflow.getComment());

        if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
            List<User> users = new ArrayList<>();

            workflow.getAssignes().forEach(uuid -> {
                User user = new User();
                user.setUuid(uuid.getUuid());
                users.add(user);
            });

            processInstance.setAssignes(users);
        }

        return ProcessInstanceRequest.builder()
                .requestInfo(requestInfo)
                .processInstances(Collections.singletonList(processInstance))
                .build();
    }

    /**
     * Get the workflow config for the given tenant
     *
     * @param tenantId    The tenantId for which businessService is requested
     * @param requestInfo The RequestInfo object of the request
     * @return BusinessService for the the given tenantId
     */
    public BusinessService getBusinessService(String tenantId, String businessService, RequestInfo requestInfo) {
        StringBuilder url = getSearchURLWithParams(tenantId, businessService);
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        Object responseObject = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
        BusinessServiceResponse response;
        try {
            response = mapper.convertValue(responseObject, BusinessServiceResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("PARSING ERROR", "Failed to parse response of workflow business service search");
        }

        if (CollectionUtils.isEmpty(response.getBusinessServices())) {
            throw new CustomException("BUSINESSSERVICE_NOT_FOUND",
                    "The businessService " + businessService + " is not found");
        }

        return response.getBusinessServices().get(0);
    }

    /**
     * Creates url for search based on given tenantId
     *
     * @param tenantId The tenantId for which url is generated
     * @return The search url
     */
    private StringBuilder getSearchURLWithParams(String tenantId, String businessService) {
        StringBuilder url = new StringBuilder(configs.getWfHost());
        url.append(configs.getWfBusinessServiceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessServices=");
        url.append(businessService);
        return url;
    }

    /**
     * Returns boolean value to specifying if the state is updatable
     *
     * @param stateCode       The stateCode of the license
     * @param businessService The BusinessService of the application flow
     * @return State object to be fetched
     */
    public Boolean isStateUpdatable(String stateCode, BusinessService businessService) {
        for (State state : businessService.getStates()) {
            if (state.getState() != null && state.getState().equalsIgnoreCase(stateCode)) {
                return state.getIsStateUpdatable();
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Creates url for searching processInstance
     *
     * @return The search url
     */
    private StringBuilder getWorkflowSearchURLWithParams(String tenantId, String businessId) {
        StringBuilder url = new StringBuilder(configs.getWfHost());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessIds=");
        url.append(businessId);
        return url;
    }

    /**
     * Fetches the workflow object for the given assessment
     *
     * @return current workflow state
     */
    public State getCurrentState(RequestInfo requestInfo, String tenantId, String businessId) {
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        StringBuilder url = getWorkflowSearchURLWithParams(tenantId, businessId);
        Object responseObject = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
        ProcessInstanceResponse response;

        try {
            response = mapper.convertValue(responseObject, ProcessInstanceResponse.class);
        } catch (Exception e) {
            throw new CustomException("PARSING_ERROR", "Failed to parse workflow search response");
        }

        if (response != null && !CollectionUtils.isEmpty(response.getProcessInstances())
                && response.getProcessInstances().get(0) != null) {
            return response.getProcessInstances().get(0).getState();
        }

        return null;
    }
}
