package org.egov.wf.web.models;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.egov.common.contract.response.ResponseInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response contract model for dashboard workflow process instance queries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardProcessInstanceResponse {

    /**
     * Response metadata info.
     */
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    /**
     * List of matching dashboard workflow process instances.
     */
    @JsonProperty("ProcessInstances")
    private List<DashboardProcessInstance> processInstances;

    /**
     * Total count of process instances matching the search criteria.
     */
    @JsonProperty("totalCount")
    private Integer totalCount;
}
