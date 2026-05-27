package org.egov.swcalculation.web.models;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BatchConnectionLog {

    @JsonProperty("id")
    private String id;

    @JsonProperty("connectionNo")
    private String connectionNo;

    @JsonProperty("taxPeriodFrom")
    private Long taxPeriodFrom;

    @JsonProperty("taxPeriodTo")
    private Long taxPeriodTo;

    @JsonProperty("insertionDate")
    private Long insertionDate;

    @JsonProperty("taxAmount")
    private BigDecimal taxAmount;

    @JsonProperty("tenantId")
    private String tenantId;
}
