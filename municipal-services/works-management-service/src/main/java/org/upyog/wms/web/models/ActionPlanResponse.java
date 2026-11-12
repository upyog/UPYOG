package org.upyog.wms.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionPlanResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("ActionPlans")
    private List<ActionPlan> actionPlans;

    @JsonProperty("Pagination")
    private Pagination pagination;

    @JsonProperty("Warnings")
    private List<ValidationWarning> warnings;
}
