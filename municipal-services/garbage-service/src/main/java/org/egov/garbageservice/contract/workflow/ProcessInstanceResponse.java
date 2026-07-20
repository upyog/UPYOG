package org.egov.garbageservice.contract.workflow;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * Wrapper for workflow service responses after a transition or process search.
 *
 * Behavior:
 * - Carries {@link org.egov.common.contract.response.ResponseInfo} and list of {@link ProcessInstance} results.
 * - Includes totalCount when the workflow API returns paginated/search metadata.
 * - Parsed from REST JSON in {@link WorkflowService#callWf} via ObjectMapper.
 * - {@link #addProceInstanceItem(ProcessInstance)} appends a process instance (method name retains legacy typo).
 *
 * Notes:
 * - GarbageAccountService reads processInstances to map businessId to applicationStatus after workflow.
 * - Null response from workflow triggers WORKFLOW_RESPONSE_NULL in WorkflowService.
 */
public class ProcessInstanceResponse {
        @JsonProperty("ResponseInfo")
        private ResponseInfo responseInfo = null;

        @JsonProperty("ProcessInstances")
        private List<ProcessInstance> processInstances = null;

        @JsonProperty("totalCount")
        private Integer totalCount = null;

        public ProcessInstanceResponse addProceInstanceItem(ProcessInstance proceInstanceItem) {
            if (this.processInstances == null) {
            this.processInstances = new ArrayList<>();
            }
        this.processInstances.add(proceInstanceItem);
        return this;
        }

}

