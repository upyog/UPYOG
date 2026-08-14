package org.egov.echallan.workflow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.echallan.enums.ChallanStatusEnum;
import org.egov.echallan.model.Challan;
import org.egov.echallan.web.models.workflow.ProcessInstance;
import org.egov.echallan.web.models.workflow.ProcessInstanceRequest;
import org.egov.echallan.web.models.workflow.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;

/**
 * Minimal client to integrate with egov-workflow-v2 service.
 * This only scaffolds the transition call; service wiring can be added later.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowIntegrator {

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${workflow.context.path:${egov.workflow.host:https://egov-workflow-v2.egov:8080}}")
  private String workflowHost;

  @Value("${workflow.transition.path:${egov.workflow.transition.path:/egov-workflow-v2/egov-wf/process/_transition}}")
  private String transitionPath;

  @Value("${challan.module.name:Challan}")
  private String moduleName;

  @Value("${challan.workflow.businessService:Challan_Generation}")
  private String defaultBusinessService;

  /**
   * Triggers a workflow transition and returns the resultant state (if
   * available).
   *
   * @param requestInfo RequestInfo
   * @param booking     BookingDetail containing businessService, tenantId,
   *                    bookingNo as businessId
   * @param action      Workflow action (e.g.,
   *                    INITIATE/APPLY/APPROVE/REJECT/CANCEL)
   * @return state after transition (nullable if not provided by wf)
   */
  public String transition(RequestInfo requestInfo, Challan booking, String action) {
    try {
      String businessService = booking.getBusinessService() != null ? booking.getBusinessService()
          : defaultBusinessService;
      ProcessInstance pi = ProcessInstance.builder()
          .businessService(businessService)
          .businessId(booking.getChallanNo())
          .tenantId(booking.getTenantId())
          .action(action)
          // optional module name used by some WF configs
          .moduleName(moduleName)
          .comment(booking.getWorkflow() != null ? booking.getWorkflow().getComment() : null)
          .build();

      ProcessInstanceRequest requestBody = ProcessInstanceRequest.builder()
          .requestInfo(requestInfo)
          .processInstances(Collections.singletonList(pi))
          .build();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<ProcessInstanceRequest> entity = new HttpEntity<>(requestBody, headers);

      URI uri = URI.create(workflowHost + transitionPath);
      ResponseEntity<ProcessInstanceResponse> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
          ProcessInstanceResponse.class);

      ProcessInstanceResponse responseBody = response.getBody();
      if (response.getStatusCode() == HttpStatus.OK && responseBody != null 
          && responseBody.getProcessInstances() != null
          && !responseBody.getProcessInstances().isEmpty()
          && responseBody.getProcessInstances().get(0).getState() != null) {
        // Extract workflow returned statuses
        String applicationStatus = responseBody.getProcessInstances().get(0).getState().getApplicationStatus();
        String state = responseBody.getProcessInstances().get(0).getState().getStateCode();

        log.info("Workflow response - Action: {}, ApplicationStatus: {}, State: {}", 
                 action, applicationStatus, state);

        // Dynamically map workflow response to challan status
        String mappedStatus = mapToBookingStatus(action, applicationStatus, state);
        if (mappedStatus != null) {
          log.info("Mapped workflow status to: {}", mappedStatus);
          return mappedStatus;
        }

        log.warn("No status mapping found for workflow response - Action: {}, ApplicationStatus: {}, State: {}", 
                 action, applicationStatus, state);
        return null;
      }
    } catch (Exception ex) {
      log.error("Workflow transition failed for bookingNo={} action={}", booking.getChallanNo(), action, ex);
    }
    return null;
  }

  // Use workflow status directly - no enum validation or mapping needed
  private String mapToBookingStatus(String action, String wfApplicationStatus, String wfState) {
    // Priority 1: Use workflow application status directly (if not null/empty)
    if (wfApplicationStatus != null && !wfApplicationStatus.trim().isEmpty()) {
      log.debug("Using workflow application status directly: {}", wfApplicationStatus);
      return wfApplicationStatus.trim();
    }
    
    // Priority 2: Use workflow state directly (if not null/empty)
    if (wfState != null && !wfState.trim().isEmpty()) {
      log.debug("Using workflow state directly: {}", wfState);
      return wfState.trim();
    }
    
    // Priority 3: Default fallback for INITIATE action only
    if (action != null && "INITIATE".equalsIgnoreCase(action.trim())) {
      log.debug("Using default INITIATE status: CHALLAN_CREATED");
      return ChallanStatusEnum.CHALLAN_CREATED.toString();
    }
    
    log.warn("No status found in workflow response - Action: {}, ApplicationStatus: {}, State: {}", 
             action, wfApplicationStatus, wfState);
    return null;
  }

}
