package org.upyog.adv.workflow;

import java.net.URI;
import java.util.Collections;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.adv.enums.BookingStatusEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.workflow.ProcessInstance;
import org.upyog.adv.web.models.workflow.ProcessInstanceRequest;
import org.upyog.adv.web.models.workflow.ProcessInstanceResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

  @Value("${adv.module.name:Advertisement}")
  private String moduleName;

  @Value("${adv.workflow.businessService:ADV-BOOKING}")
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
  public String transition(RequestInfo requestInfo, BookingDetail booking, String action) {
    try {
      String businessService = booking.getBusinessService() != null ? booking.getBusinessService()
          : defaultBusinessService;
      ProcessInstance pi = ProcessInstance.builder()
          .businessService(businessService)
          .businessId(booking.getBookingNo())
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
      if (responseBody != null && responseBody.getProcessInstances() != null
          && !responseBody.getProcessInstances().isEmpty()
          && responseBody.getProcessInstances().get(0).getState() != null) {
        // Extract workflow returned statuses
        String applicationStatus = responseBody.getProcessInstances().get(0).getState().getApplicationStatus();
        String state = responseBody.getProcessInstances().get(0).getState().getState();

        // First: map the action to our BookingStatusEnum (minimal surface change)
        String mapped = mapToBookingStatus(action, applicationStatus, state);
        if (mapped != null) return mapped;

        // Next: if WF returned status already matches our enum, allow it
        if (isValidEnum(applicationStatus)) return applicationStatus;
        if (isValidEnum(state)) return state;
      }
    } catch (Exception ex) {
      log.error("Workflow transition failed for bookingNo={} action={}", booking.getBookingNo(), action, ex);
    }
    return null;
  }

  // Map workflow action or returned status to our existing enums without broader code changes
  private String mapToBookingStatus(String action, String wfApplicationStatus, String wfState) {
    String act = action == null ? null : action.trim().toUpperCase();
    if (act != null) {
      switch (act) {
        case "INITIATE":
          return BookingStatusEnum.BOOKING_CREATED.toString();
        case "SUBMIT":
          return BookingStatusEnum.PENDING_FOR_PAYMENT.toString();
        case "PAY":
          return BookingStatusEnum.BOOKED.toString();
          case "PENDING_FOR_INSPECTION":
              return BookingStatusEnum.PENDING_FOR_INSPECTION.toString();
        case "APPROVE":
          return BookingStatusEnum.BOOKED.toString();
        case "CANCEL":
          return BookingStatusEnum.CANCELLED.toString();
        case "REQUEST_CANCELLATION":
          return BookingStatusEnum.CANCELLATION_REQUESTED.toString();
        default:
          break;
      }
    }

    // Fallbacks using WF-provided status names when they align with our enums
    if (isValidEnum(wfApplicationStatus)) return wfApplicationStatus;
    if (isValidEnum(wfState)) return wfState;
    return null;
  }

  private boolean isValidEnum(String status) {
    if (status == null || status.isEmpty()) return false;
    try {
      BookingStatusEnum.valueOf(status);
      return true;
    } catch (Exception ignore) {
      return false;
    }
  }
}
