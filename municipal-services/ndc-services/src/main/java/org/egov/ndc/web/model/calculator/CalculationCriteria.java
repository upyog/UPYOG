package org.egov.ndc.web.model.calculator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalculationCriteria {
    @JsonProperty("ndcapplication")
    private NdcApplicationRequest ndcApplicationRequest = null;

    @JsonProperty("applicationNumber")
    private String applicationNumber = null;

    @JsonProperty("tenantId")
    private String tenantId = null;

    @JsonProperty("propertyType")
    private String propertyType = null;


}