package org.upyog.tp.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.tp.config.TreePruningConfiguration;
import org.upyog.tp.constant.TreePruningConstants;
import org.upyog.tp.repository.ServiceRequestRepository;
import org.upyog.tp.web.models.treePruning.TreePruningBookingDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;
import org.upyog.tp.web.models.Workflow;
import org.upyog.tp.web.models.workflow.ProcessInstance;
import org.upyog.tp.web.models.workflow.ProcessInstanceRequest;
import org.upyog.tp.web.models.workflow.ProcessInstanceResponse;
import org.upyog.tp.web.models.workflow.State;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.PaymentRequest;

@Service
public class WorkflowService {

    @Autowired
    private TreePruningConfiguration configs;

    @Autowired
    private ServiceRequestRepository restRepo;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    ServiceRequestRepository serviceRequestRepository;

    public State updateWorkflowStatus(PaymentRequest paymentRequest, TreePruningBookingRequest treePruningRequest) {
        ProcessInstance processInstance;
        RequestInfo requestInfo;

        if (paymentRequest != null) {
            processInstance = getProcessInstanceForTP(paymentRequest, null, null);
            requestInfo = paymentRequest.getRequestInfo();
        } else if (treePruningRequest != null) {
            processInstance = getProcessInstanceForTP(null, treePruningRequest.getTreePruningBookingDetail(), treePruningRequest.getRequestInfo());
            requestInfo = treePruningRequest.getRequestInfo();
        } else {
            throw new IllegalArgumentException("Both PaymentRequest and TreePruningBookingRequest cannot be null");
        }
        ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(requestInfo, Collections.singletonList(processInstance));

        return callWorkFlow(workflowRequest);
    }

    private ProcessInstance getProcessInstanceForTP(PaymentRequest paymentRequest, TreePruningBookingDetail application, RequestInfo requestInfo) {
        ProcessInstance processInstance = new ProcessInstance();

        if (paymentRequest != null) {
            processInstance.setBusinessId(paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode());
            processInstance.setAction(TreePruningConstants.ACTION_PAY);
            processInstance.setModuleName(configs.getTpModuleName());
            processInstance.setTenantId(paymentRequest.getPayment().getTenantId());
            processInstance.setBusinessService(configs.getTreePruningBusinessService());
            processInstance.setDocuments(null);
            processInstance.setComment(null);
            processInstance.setAssignes(null);
        } else if (application != null) {
            Workflow workflow = application.getWorkflow();
            processInstance.setBusinessId(application.getBookingNo());
            processInstance.setAction(workflow.getAction());
            processInstance.setModuleName(workflow.getModuleName());
            processInstance.setTenantId(application.getTenantId());
            processInstance.setBusinessService(workflow.getBusinessService());
            processInstance.setDocuments(workflow.getDocuments());
            processInstance.setComment(workflow.getComments());

            if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
                List<User> users = workflow.getAssignes().stream().map(uuid -> {
                    User user = new User();
                    user.setUuid(uuid);
                    return user;
                }).collect(Collectors.toList());

                processInstance.setAssignes(users);
            }
        } else {
            throw new IllegalArgumentException("Both PaymentRequest and TreePruningBookingDetail cannot be null");
        }

        return processInstance;
    }

    /**
     * Method to integrate with workflow
     *
     * takes the Tree Pruning request as parameter constructs the work-flow request
     *
     * and sets the resultant status from wf-response back to trade-license object
     *
     */
    public State callWorkFlow(ProcessInstanceRequest workflowReq) {

        ProcessInstanceResponse response = null;
        StringBuilder url = new StringBuilder(configs.getWfHost().concat(configs.getWfTransitionPath()));
        Object optional = serviceRequestRepository.fetchResult(url, workflowReq);
        response = mapper.convertValue(optional, ProcessInstanceResponse.class);
        return response.getProcessInstances().get(0).getState();
    }



}