package org.egov.garbageservice.contract.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
/**
 * REST request body for triggering workflow transitions on the eGov workflow service.
 *
 * Behavior:
 * - Wraps {@link org.egov.common.contract.request.RequestInfo} and a list of {@link ProcessInstance} to transition.
 * - Posted by {@link WorkflowService#callWf(ProcessInstanceRequest)} to the workflow transition endpoint.
 * - {@link #addProcessInstanceItem(ProcessInstance)} appends another instance (e.g. parent + sub-accounts).
 *
 * Notes:
 * - JSON keys use PascalCase ({@code RequestInfo}, {@code ProcessInstances}) per workflow contract.
 * - Built in GarbageAccountService when account create/update/status change runs workflow.
 */
public class ProcessInstanceRequest {
        @JsonProperty("RequestInfo")
        private RequestInfo requestInfo;

        @JsonProperty("ProcessInstances")
//        @Valid
//        @NotNull
        private List<ProcessInstance> processInstances;


        public ProcessInstanceRequest addProcessInstanceItem(ProcessInstance processInstanceItem) {
            if (this.processInstances == null) {
            this.processInstances = new ArrayList<>();
            }
        this.processInstances.add(processInstanceItem);
        return this;
        }

}

