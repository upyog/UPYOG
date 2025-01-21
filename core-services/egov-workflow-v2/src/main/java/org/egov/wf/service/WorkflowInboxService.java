package org.egov.wf.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.wf.web.models.Action;
import org.egov.wf.web.models.ProcessInstance;
import org.egov.wf.web.models.ProcessInstanceSearchCriteria;
import org.egov.wf.web.models.ValidActionResponce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class WorkflowInboxService {

	@Autowired
	private WorkflowService workflowService;

	public ValidActionResponce getValidAction(RequestInfo info, String businessId, String tenantId) {
		ValidActionResponce validActionResponse = ValidActionResponce.builder()
				.responseInfo(ResponseInfo.builder().build()).build();

		List<ProcessInstance> instances = workflowService.search(info, ProcessInstanceSearchCriteria.builder()
				.businessIds(Arrays.asList(businessId)).tenantId(tenantId).build());

		if (instances == null || instances.size() < 1) {
			return validActionResponse;
		}
		ProcessInstance instance = instances.get(0);

		validActionResponse.setBusinessService(instance.getBusinessService());
		validActionResponse.setModuleName(instance.getModuleName());

		List<Action> validAction = new ArrayList<>();
		List<String> actionList = new ArrayList<>();
		if (instance.getState().getIsTerminateState()) {
			validActionResponse.setNextValidAction(validAction);

			return validActionResponse;
		}

		User loggedinUser = info.getUserInfo();
		Set<String> loggedinUserRoles = loggedinUser.getRoles().stream()
				.filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)
						|| role.getCode().equalsIgnoreCase("CITIZEN"))
				.map(Role::getCode).collect(Collectors.toSet());

		instance.getNextActions().stream().forEach(action -> {
			Set<String> actionRolesSet = new HashSet<>(action.getRoles());
			if (actionRolesSet.stream().anyMatch(loggedinUserRoles::contains)) {
				validAction.add(action);
				actionList.add(action.getAction());
			}
		});

		validActionResponse.setNextValidAction(validAction);
		validActionResponse.setAction(actionList);

		validActionResponse.setIsUpdatable(instance != null && instance.getState() != null
				&& !CollectionUtils.isEmpty(validActionResponse.getNextValidAction())
						? BooleanUtils.toBoolean(instance.getState().getIsStateUpdatable())
						: false);

		return validActionResponse;
	}

}
