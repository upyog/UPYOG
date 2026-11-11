package org.upyog.wms.service;

import static org.upyog.wms.util.WmsUtil.isBlank;
import static org.upyog.wms.util.WmsUtil.nn;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.wms.config.WmsConfiguration;
import org.upyog.wms.constants.ActionPlanAction;
import org.upyog.wms.constants.ActionPlanStatus;
import org.upyog.wms.constants.ErrorCodes;
import org.upyog.wms.repository.ServiceRequestRepository;
import org.upyog.wms.web.models.ActionPlan;
import org.upyog.wms.web.models.Workflow;
import org.upyog.wms.web.models.mdms.MdmsData;
import org.upyog.wms.web.models.workflow.ProcessInstance;
import org.upyog.wms.web.models.workflow.ProcessInstanceRequest;
import org.upyog.wms.web.models.workflow.ProcessInstanceResponse;

/**
 * Moves an Action Plan to its next status.
 * Workflow ON : egov-workflow-v2 does the transition, role check and SLA; history stays there.
 * Workflow OFF: the small state machine below is used. Plans are already approved offline, so a
 *               submission publishes the plan directly.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

    private static final Map<ActionPlanStatus, Map<ActionPlanAction, ActionPlanStatus>> LOCAL_TRANSITIONS;

    static {
        Map<ActionPlanStatus, Map<ActionPlanAction, ActionPlanStatus>> t = new EnumMap<>(ActionPlanStatus.class);
        t.put(ActionPlanStatus.INITIATED, Map.of(
                ActionPlanAction.SUBMIT, ActionPlanStatus.APPROVED,
                ActionPlanAction.SUBMIT_TO_APPROVER, ActionPlanStatus.APPROVED,
                ActionPlanAction.WITHDRAW, ActionPlanStatus.WITHDRAWN));
        t.put(ActionPlanStatus.RETURNED, Map.of(
                ActionPlanAction.RESUBMIT, ActionPlanStatus.APPROVED,
                ActionPlanAction.RESUBMIT_TO_APPROVER, ActionPlanStatus.APPROVED,
                ActionPlanAction.WITHDRAW, ActionPlanStatus.WITHDRAWN));
        LOCAL_TRANSITIONS = t;
    }

    private final ServiceRequestRepository serviceRequestRepository;
    private final WmsConfiguration config;

    /** Per-state MDMS switch wins over the service property. */
    public boolean isWorkflowEnabled(MdmsData mdms) {
        Boolean stateSetting = mdms.config().workflowEnabled();
        return stateSetting != null ? stateSetting : Boolean.TRUE.equals(config.getWorkflowEnabled());
    }

    /**
     * @param currentStatus null for a new application
     * @return the new application status
     */
    public String transition(RequestInfo requestInfo, ActionPlan plan, ActionPlanAction action, String currentStatus,
                             boolean workflowEnabled) {
        if (!workflowEnabled) {
            return localTransition(action, currentStatus);
        }
        ProcessInstanceRequest request = ProcessInstanceRequest.builder()
                .requestInfo(requestInfo)
                .processInstances(List.of(processInstance(plan, action)))
                .build();
        ProcessInstanceResponse response = serviceRequestRepository.post(
                config.getWorkflowHost() + config.getWorkflowTransitionPath(), request, ProcessInstanceResponse.class);
        return Optional.ofNullable(response)
                .map(ProcessInstanceResponse::getProcessInstances)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0).getState())
                .map(s -> isBlank(s.getApplicationStatus()) ? s.getState() : s.getApplicationStatus())
                .orElseThrow(() -> new CustomException(ErrorCodes.EXTERNAL_SERVICE,
                        "Workflow transition returned no state for " + plan.getApplicationNumber()));
    }

    private String localTransition(ActionPlanAction action, String currentStatus) {
        if (action == ActionPlanAction.INITIATE && currentStatus == null) {
            return ActionPlanStatus.INITIATED.name();
        }
        return ActionPlanStatus.from(currentStatus)
                .map(LOCAL_TRANSITIONS::get)
                .map(m -> m.get(action))
                .map(Enum::name)
                .orElseThrow(() -> new CustomException(ErrorCodes.INVALID_ACTION, "Action " + action
                        + " is not allowed in status " + currentStatus));
    }

    private ProcessInstance processInstance(ActionPlan plan, ActionPlanAction action) {
        Workflow wf = Optional.ofNullable(plan.getWorkflow()).orElseGet(Workflow::new);
        List<User> assignees = nn(wf.getAssignes()).stream().map(uuid -> {
            User user = new User();
            user.setUuid(uuid);
            return user;
        }).toList();

        Map<String, Object> decision = new LinkedHashMap<>();
        putIfPresent(decision, "approvingDepartment", wf.getApprovingDepartment());
        putIfPresent(decision, "designation", wf.getDesignation());
        putIfPresent(decision, "approverName", wf.getApproverName());

        return ProcessInstance.builder()
                .tenantId(plan.getTenantId())
                .businessService(config.getBusinessService())
                .moduleName(config.getModuleName())
                .businessId(plan.getApplicationNumber())
                .action(action.name())
                .comment(wf.getComments())
                .documents(wf.getDocuments())
                .assignes(assignees.isEmpty() ? null : assignees)
                .additionalDetails(decision.isEmpty() ? null : decision)
                .build();
    }

    private static void putIfPresent(Map<String, Object> map, String key, String value) {
        if (!isBlank(value)) {
            map.put(key, value);
        }
    }
}
