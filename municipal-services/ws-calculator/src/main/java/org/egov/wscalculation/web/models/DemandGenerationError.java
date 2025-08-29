package org.egov.wscalculation.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class DemandGenerationError {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("connectionNo")
    private String connectionNo;
    @JsonProperty("tenantId")
    private String tenantId;
    @JsonProperty("toDate")
    private Long toDate;
    @JsonProperty("fromDate")
    private Long fromDate;
    @JsonProperty("assessmentYear")
    private String assessmentYear;
    @JsonProperty("propertyId")
    private String propertyId;
    @JsonProperty("waterSubUsageType")
    private String waterSubUsageType;
    @JsonProperty("errorMessage")
    private String errorMessage;


}

