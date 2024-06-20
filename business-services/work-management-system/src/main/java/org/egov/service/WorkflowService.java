package org.egov.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.config.WMSWorkEstimationConfiguration;
import org.egov.repository.ServiceRequestRepository;
import org.egov.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSWorkEstimationConfiguration;
import org.egov.repository.WMSWorkEstimationRepository;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.WMSWorkAwardApprovalRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class WorkflowService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository repository;

    @Autowired
    private WMSWorkEstimationConfiguration config;

    public void updateWorkflowStatus(WMSWorkEstimationRequest workEstimationRequest) {
        workEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
            ProcessInstance processInstance = getProcessInstanceForWork(application, workEstimationRequest.getRequestInfo());
            ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(workEstimationRequest.getRequestInfo(), Collections.singletonList(processInstance));
            callWorkFlow(workflowRequest);
        });
    }

    public State callWorkFlow(ProcessInstanceRequest workflowReq) {

        ProcessInstanceResponse response = null;
        StringBuilder url = new StringBuilder(config.getWfHost().concat(config.getWfTransitionPath()));
        Object optional = repository.fetchResult(url, workflowReq);
        response = mapper.convertValue(optional, ProcessInstanceResponse.class);
        return response.getProcessInstances().get(0).getState();
    }

    private ProcessInstance getProcessInstanceForWork(WMSWorkEstimationApplication application, RequestInfo requestInfo) {
        Workflow workflow = application.getWorkflow();

        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBusinessId(application.getWorkEstimationNo());
        processInstance.setAction(workflow.getAction());
        processInstance.setModuleName("WMS-services");
        processInstance.setTenantId(application.getTenantId());
        processInstance.setBusinessService("WorkEstimation");
        processInstance.setDocuments(workflow.getDocuments());
        processInstance.setComment(workflow.getComments());

        if(!CollectionUtils.isEmpty(workflow.getAssignes())){
            List<User> users = new ArrayList<>();

            workflow.getAssignes().forEach(uuid -> {
                User user = new User();
                user.setUuid(uuid);
                users.add(user);
            });

            processInstance.setAssignes(users);
        }

        return processInstance;

    }

    private BusinessService getBusinessService(WMSWorkEstimationApplication application, RequestInfo requestInfo) {
        String tenantId = application.getTenantId();
        StringBuilder url = getSearchURLWithParams(tenantId, "WMS");
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        Object result = repository.fetchResult(url, requestInfoWrapper);
        BusinessServiceResponse response = null;
        try {
            response = mapper.convertValue(result, BusinessServiceResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("PARSING ERROR", "Failed to parse response of workflow business service search");
        }

        if (CollectionUtils.isEmpty(response.getBusinessServices()))
            throw new CustomException("BUSINESSSERVICE_NOT_FOUND", "The businessService " + "WMS" + " is not found");

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

    public ProcessInstanceRequest getProcessInstanceForBirthRegistrationPayment(WMSWorkEstimationRequest updateRequest) {

    	WMSWorkEstimationApplication application = updateRequest.getWmsWorkEstimationApplications().get(0);

        ProcessInstance process = ProcessInstance.builder()
                //.businessService("BTR")
        		.businessService("WMS")
                .businessId(application.getWorkEstimationNo())
                .comment("Payment for Work Management System processed")
                //.moduleName("birth-services")
                .moduleName("WMS-services")
                .tenantId(application.getTenantId())
                .action("PAY")
                .build();

        return ProcessInstanceRequest.builder()
                .requestInfo(updateRequest.getRequestInfo())
                .processInstances(Arrays.asList(process))
                .build();

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

    public ProcessInstance getCurrentWorkflow(RequestInfo requestInfo, String tenantId, String businessId) {

        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

        StringBuilder url = getWorkflowSearchURLWithParams(tenantId, businessId);

        Object res = repository.fetchResult(url, requestInfoWrapper);
        ProcessInstanceResponse response = null;

        try{
            response = mapper.convertValue(res, ProcessInstanceResponse.class);
        }
        catch (Exception e){
            throw new CustomException("PARSING_ERROR","Failed to parse workflow search response");
        }

        if(response!=null && !CollectionUtils.isEmpty(response.getProcessInstances()) && response.getProcessInstances().get(0)!=null)
            return response.getProcessInstances().get(0);

        return null;
    }

}