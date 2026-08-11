package org.egov.garbageservice.web.models.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.config.GarbageServiceConfig;
import org.egov.garbageservice.web.models.GarbageAccountActionRequest;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.garbageservice.util.RestCallRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;

/**
 * Client service that calls the external eGov workflow engine for garbage account processes.
 * <p>
 * Behavior:
 * - {@link #callWf(ProcessInstanceRequest)} — POST workflow transition; returns {@link ProcessInstanceResponse}.
 * - {@link #businessServiceSearch} — GET business service definition (states/actions) for a tenant and service code.
 * - {@link #getValidAction} — POST to fetch allowed actions and isUpdatable for a businessId and tenantId.
 * - Uses {@link GrbgConstants} for host/paths, {@link RestCallRepository} or {@link RestTemplate}, and ObjectMapper.
 * <p>
 * Notes:
 * - Does not store workflow state locally; persistence is on the workflow service.
 * - callWf throws {@link CustomException} WORKFLOW_RESPONSE_NULL when the response cannot be parsed.
 * - getValidAction logs errors and may return an empty {@link ValidActionResponce} on non-2xx responses.
 */
@Slf4j
@Service
public class WorkflowService {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private RestCallRepository restCallRepository;
    @Autowired
    private GrbgConstants applicationPropertiesAndConstant;
    @Autowired
    private GarbageServiceConfig garbageServiceConfig;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Dispatches a process transition request to the eGov workflow engine.
     *
     * @param processInstanceRequest the payload containing workflow transition data
     * @return the workflow engine's response acknowledging the state change
     */

    public ProcessInstanceResponse callWf(ProcessInstanceRequest processInstanceRequest) {
        StringBuilder url = new StringBuilder(garbageServiceConfig.getWorkflowHost());
        url.append(garbageServiceConfig.workflowEndpointTransition);
        Object response = restCallRepository.fetchResult(url, processInstanceRequest);
        ProcessInstanceResponse processInstanceResponse = objectMapper.convertValue(response, ProcessInstanceResponse.class);

        if (null == response || null == processInstanceResponse) {
            throw new CustomException("WORKFLOW_RESPONSE_NULL", "Error ocurred while running workflow.");
        }

        return processInstanceResponse;
    }


    /**
     * Retrieves the workflow state machine configuration (BusinessService) for a given tenant.
     *
     * @param garbageAccountActionRequest the context containing authentication info
     * @param tenantId                    the tenant ID for which the business service is configured
     * @param businessService             the exact name of the workflow business service
     * @return the resolved {@link BusinessServiceResponse} detailing states and actions
     */

    public BusinessServiceResponse businessServiceSearch(GarbageAccountActionRequest garbageAccountActionRequest,
                                                         String applicationTenantId, String applicationBusinessId) {
        StringBuilder uri = new StringBuilder(garbageServiceConfig.getWorkflowHost());
        uri.append(garbageServiceConfig.getWorkflowBusinessServiceSearchPath());
        uri.append("?tenantId=").append(applicationTenantId);
        uri.append("&businessServices=").append(applicationBusinessId);
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder()
                .requestInfo(garbageAccountActionRequest.getRequestInfo()).build();
        LinkedHashMap<String, Object> responseObject = (LinkedHashMap<String, Object>) restCallRepository.fetchResult(uri, requestInfoWrapper);
        BusinessServiceResponse businessServiceResponse = objectMapper.convertValue(responseObject
                , BusinessServiceResponse.class);
        return businessServiceResponse;
    }

    /**
     * Evaluates if a specified action is permissible within a given workflow business service.
     *
     * @param tenantId        the context tenant ID
     * @param businessService the workflow business service to evaluate against
     * @param action          the proposed workflow action string
     * @return a {@link ValidActionResponce} containing the validation result
     */

    public ValidActionResponce getValidAction(RequestInfo requestInfo, String businessId, String tenantId) {

        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromHttpUrl(garbageServiceConfig.getWorkflowHost())
                    .path(garbageServiceConfig.getWorkflowValidActionSearchPath());

            if (StringUtils.isNotEmpty(businessId)) {
                uriBuilder.queryParam("businessId", businessId);
            }
            if (StringUtils.isNotEmpty(tenantId)) {
                uriBuilder.queryParam("tenantId", tenantId);
            }

            String url = uriBuilder.toUriString();

            ResponseEntity<ValidActionResponce> response = restTemplate.postForEntity(url.toString(),
                    RequestInfoWrapper.builder().requestInfo(requestInfo).build(), ValidActionResponce.class);
            // Check the response status and return the body
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
            }

        } catch (Exception e) {
            log.error("Exception while calling wf action: ", e);
            throw new CustomException("ERR_TECHNICAL", "Invalid response format from external API");
        }
        return ValidActionResponce.builder().build();
    }


}
