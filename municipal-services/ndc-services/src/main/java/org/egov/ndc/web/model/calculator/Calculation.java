package org.egov.ndc.web.model.calculator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Calculation {
    @JsonProperty("applicationNumber")
    private String applicationNumber = null;

    @JsonProperty("totalAmount")
    private Double totalAmount = null;

    @JsonProperty("tenantId")
    private String tenantId = null;


}