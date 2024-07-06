package digit.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.egov.common.contract.models.RequestInfoWrapper;
import org.egov.common.contract.models.Workflow;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.contract.workflow.State;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.config.BmcConfiguration;
import digit.repository.ServiceRequestRepository;
import digit.web.models.BusinessService;
import digit.web.models.BusinessServiceResponse;
import digit.web.models.ProcessInstance;
import digit.web.models.ProcessInstanceRequest;
import digit.web.models.ProcessInstanceResponse;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WorkflowService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository repository;

    @Autowired
    private BmcConfiguration config;

    public void updateWorkflowStatus(SchemeApplicationRequest schemeApplicationRequest) {
        schemeApplicationRequest.getSchemeApplications().forEach(application -> {
            ProcessInstance processInstance = getProcessInstanceForSchemeApplication(application, schemeApplicationRequest.getRequestInfo());
            ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(schemeApplicationRequest.getRequestInfo(), Collections.singletonList(processInstance));
            callWorkFlow(workflowRequest);
        });
    }

    public State callWorkFlow(ProcessInstanceRequest workflowReq) {
        ProcessInstanceResponse response = null;
        StringBuilder url = new StringBuilder(config.getWfHost().concat(config.getWfTransitionPath()));
        Object optional = repository.fetchResult(url, workflowReq);
        if (optional == null) {
            throw new CustomException("WORKFLOW_ERROR", "Workflow service response is null");
        }
        response = mapper.convertValue(optional, ProcessInstanceResponse.class);
        return response.getProcessInstances().get(0).getState();
    }

    private ProcessInstance getProcessInstanceForSchemeApplication(SchemeApplication application, RequestInfo requestInfo) {
        Workflow workflow = application.getWorkflow();

        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBusinessId(application.getApplicationNumber());
        processInstance.setAction(workflow.getAction());
        processInstance.setModuleName("bmc-schemes");
        processInstance.setTenantId(application.getTenantId());
        processInstance.setBusinessService("BMC");
        processInstance.setDocuments(workflow.getDocuments());
        processInstance.setComment(workflow.getComments());

        if(!CollectionUtils.isEmpty(workflow.getAssignes())){
            List<User> users = new ArrayList<>();

            workflow.getAssignes().forEach(uuid -> {
                User user = new User();
                user.setUuid(uuid);
                users.add(user);
            });

            processInstance.setAssignes(null);
        }

        return processInstance;
    }

    public ProcessInstance getCurrentWorkflow(RequestInfo requestInfo, String tenantId, String businessId) {
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        StringBuilder url = getSearchURLWithParams(tenantId, businessId);
        Object res = repository.fetchResult(url, requestInfoWrapper);
        if (res == null) {
            throw new CustomException("WORKFLOW_ERROR", "Workflow service response is null");
        }
        ProcessInstanceResponse response = mapper.convertValue(res, ProcessInstanceResponse.class);
        if(response != null && !CollectionUtils.isEmpty(response.getProcessInstances()) && response.getProcessInstances().get(0) != null)
            return response.getProcessInstances().get(0);
        return null;
    }

    private BusinessService getBusinessService(SchemeApplication application, RequestInfo requestInfo) {
        String tenantId = application.getTenantId();
        StringBuilder url = getSearchURLWithParams(tenantId, "BMC_SCHEME_APPLICATION");
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        Object result = repository.fetchResult(url, requestInfoWrapper);
        if (result == null) {
            throw new CustomException("WORKFLOW_ERROR", "Workflow service response is null");
        }
        BusinessServiceResponse response = mapper.convertValue(result, BusinessServiceResponse.class);
        if (CollectionUtils.isEmpty(response.getBusinessServices()))
            throw new CustomException("BUSINESSSERVICE_NOT_FOUND", "The businessService BMC_SCHEME_APPLICATION is not found");
        return response.getBusinessServices().get(0);
    }

    private StringBuilder getSearchURLWithParams(String tenantId, String businessService) {
        StringBuilder url = new StringBuilder(config.getWfHost());
        url.append(config.getWfBusinessServiceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessServices=");
        url.append(businessService);
        return url;
    }

    public Boolean isStateUpdatable(String stateCode, BusinessService businessService) {
        for (State state : businessService.getStates()) {
            if (state.getState() != null && state.getState().equalsIgnoreCase(stateCode))
                return state.getIsStateUpdatable();
        }
        return false;
    }

    private StringBuilder getWorkflowSearchURLWithParams(String tenantId, String businessId) {
        StringBuilder url = new StringBuilder(config.getWfHost());
        url.append(config.getWfProcessInstanceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessIds=");
        url.append(businessId);
        return url;
    }

    public State getCurrentState(RequestInfo requestInfo, String tenantId, String businessId) {
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        StringBuilder url = getWorkflowSearchURLWithParams(tenantId, businessId);
        Object res = repository.fetchResult(url, requestInfoWrapper);
        if (res == null) {
            throw new CustomException("WORKFLOW_ERROR", "Workflow service response is null");
        }
        ProcessInstanceResponse response = mapper.convertValue(res, ProcessInstanceResponse.class);
        if (response != null && !CollectionUtils.isEmpty(response.getProcessInstances())
                && response.getProcessInstances().get(0) != null)
            return response.getProcessInstances().get(0).getState();
        return null;
    }
}
