package org.egov.swcalculation.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BatchDemandLog {

    @JsonProperty("id")
    private String id;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("taxPeriodFrom")
    private Long taxPeriodFrom;

    @JsonProperty("taxPeriodTo")
    private Long taxPeriodTo;

    @JsonProperty("insertionTime")
    private Long insertionTime;

    @JsonProperty("totalConnectionCount")
    private Long totalConnectionCount;

    @JsonProperty("isDemandExecuted")
    private Boolean isDemandExecuted;
}
