package org.egov.pqm.workflow;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.pqm.web.model.Test;
import org.egov.pqm.web.model.TestRequest;
import org.egov.pqm.web.model.workflow.Action;
import org.egov.pqm.web.model.workflow.BusinessService;
import org.egov.pqm.web.model.workflow.State;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.egov.pqm.util.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Component
public class ActionValidator {


	private WorkflowService workflowService;

	@Autowired
	public ActionValidator(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	

	/**
	 * Validates the update request
	 * 
	 * @param testRequest
	 * @param businessService
	 *            The PQM update request
	 */
	public void validateUpdateRequest(TestRequest testRequest, BusinessService businessService) {
		validateRoleAction(testRequest,businessService);
		validateIds(testRequest, businessService);
	}

	/**
	 * Validates if the role of the logged in user can perform the given action
	 * 
	 * @param testRequest
	 *            The pqm create or update request
	 */
	private void validateRoleAction(TestRequest testRequest, BusinessService businessService) {
		Test test = testRequest.getTests().get(0);
		Map<String, String> errorMap = new HashMap<>();
		RequestInfo requestInfo = testRequest.getRequestInfo();
		State state = workflowService.getCurrentStateObj(String.valueOf(test.getWfStatus()),businessService);
		if(state != null ) {
			List<Action> actions = state.getActions();
			List<Role> roles = requestInfo.getUserInfo().getRoles();
			List<String> validActions = new LinkedList<>();
			
			roles.forEach(role -> {
				actions.forEach(action -> {
					if (action.getRoles().contains(role.getCode())) {
						validActions.add(action.getAction());
					}
				});
			});

			if (!validActions.contains(test.getWorkflow().getAction())) {
				errorMap.put("UNAUTHORIZED UPDATE", "The action cannot be performed by this user");
			}
		}else {
			errorMap.put("UNAUTHORIZED UPDATE", "No workflow state configured for the current status of the application");
		}
		
		if (!errorMap.isEmpty()) {
			throw new CustomException(errorMap);
		}
			
	}

	/**
	 * Validates if the any new object is added in the request
	 * 
	 * @param request
	 * @param businessService
	 *            The pqm update request
	 */
	private void validateIds(TestRequest request, BusinessService businessService) {
		Map<String, String> errorMap = new HashMap<>();
		Test test = request.getTests().get(0);
		
		if( !workflowService.isStateUpdatable(String.valueOf(test.getStatus()), businessService)&& test.getTestId() == null) {
				errorMap.put(ErrorConstants.UPDATE_ERROR, "Id of Application cannot be null");
		}
		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}


}
