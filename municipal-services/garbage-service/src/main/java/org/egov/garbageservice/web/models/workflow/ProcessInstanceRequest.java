package org.egov.garbageservice.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

import java.util.ArrayList;
import java.util.List;

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


    /**
     * Appends a process instance to the workflow transition request list.
     *
     * @param processInstanceItem the process instance to append
     * @return this ProcessInstanceRequest instance for chaining
     */

    public ProcessInstanceRequest addProcessInstanceItem(ProcessInstance processInstanceItem) {
        if (this.processInstances == null) {
            this.processInstances = new ArrayList<>();
        }
        this.processInstances.add(processInstanceItem);
        return this;
    }

}

