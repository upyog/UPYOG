package org.egov.refund.service.impl;

import java.util.Collections;
import java.util.List;

import org.egov.common.contract.workflow.ProcessInstance;
import org.egov.common.contract.workflow.ProcessInstanceRequest;
import org.egov.common.contract.workflow.ProcessInstanceResponse;
import org.egov.common.contract.workflow.State;
import org.egov.refund.Repository.ServiceRequestRepository;
import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.model.Refund;
import org.egov.refund.model.WorkflowTransition;
import org.egov.refund.service.WorkflowService;
import org.egov.refund.web.contracat.RefundActionRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WorkflowServiceImpl implements WorkflowService {

	private final ApplicationProperties config;
	private final ServiceRequestRepository serviceRequestRepository;
	private final ObjectMapper mapper;

	public WorkflowServiceImpl(ApplicationProperties config, ServiceRequestRepository serviceRequestRepository,
			ObjectMapper mapper) {

		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.mapper = mapper;
	}

	@Override
	public WorkflowTransition validateAndGetNextState(Refund refund, RefundActionRequest request) {

		ProcessInstanceRequest workflowRequest = getProcessInstanceRequest(refund, request);

		State nextState = callWorkFlow(workflowRequest);

		if (nextState == null) {
			throw new CustomException("INVALID_WORKFLOW", "Workflow transition failed.");
		}

		return WorkflowTransition.builder().valid(true).currentState(refund.getStatus()).action(request.getAction())
				.nextState(nextState.getState()).tenantId(refund.getTenantId()).applicationStatus(nextState.getApplicationStatus())
				.businessService(refund.getBusinessService()).build();
	}

	private ProcessInstanceRequest getProcessInstanceRequest(Refund refund, RefundActionRequest request) {

		if (request == null) {
			throw new CustomException("INVALID_REQUEST", "Refund action request is mandatory.");
		}

		if (request.getRequestInfo() == null) {
			throw new CustomException("INVALID_REQUEST", "RequestInfo is mandatory.");
		}

		if (isBlank(request.getAction())) {
			throw new CustomException("INVALID_WORKFLOW", "Workflow action is mandatory.");
		}

		ProcessInstance processInstance = ProcessInstance.builder().businessId(refund.getRefundNo())
				.tenantId(refund.getTenantId()).businessService(refund.getBusinessService())
				.moduleName(refund.getModuleName()).action(request.getAction()).comment(request.getRemarks())
				.documents(Collections.emptyList()).assignes(Collections.emptyList()).build();

		return ProcessInstanceRequest.builder().requestInfo(request.getRequestInfo())
				.processInstances(List.of(processInstance)).build();
	}

	private State callWorkFlow(ProcessInstanceRequest workflowRequest) {

		StringBuilder url = new StringBuilder(config.getWfHost().concat(config.getWfTransitionPath()));

		Object responseObject = serviceRequestRepository.fetchResult(url, workflowRequest);

		ProcessInstanceResponse response = mapper.convertValue(responseObject, ProcessInstanceResponse.class);

		if (response == null || response.getProcessInstances() == null || response.getProcessInstances().isEmpty()) {

			throw new CustomException("INVALID_WORKFLOW", "No workflow transition response received.");
		}

		return response.getProcessInstances().get(0).getState();
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}