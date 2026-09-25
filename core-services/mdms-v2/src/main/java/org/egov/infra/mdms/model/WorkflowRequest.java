package org.egov.infra.mdms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.egov.common.contract.request.RequestInfo;

import java.util.List;

@Data
public class WorkflowRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("ProcessInstances")
    private List<WorkflowProcessInstance> processInstances;
}
