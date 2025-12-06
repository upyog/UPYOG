package org.egov.pqm.anomaly.finder.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PqmAnomalySearchRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo = null;

    @JsonProperty("PqmAnomalySearchCriteria")
    private PqmAnomalySearchCriteria pqmAnomalySearchCriteria;
}
