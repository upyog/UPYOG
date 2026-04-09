package org.egov.layout.web.model.calculator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import org.egov.layout.web.model.Clu;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalculationCriteria {
	@JsonProperty("CLU")
	private Clu layout = null;

    @JsonProperty("applicationNumber")
    private String applicationNumber = null;

    @JsonProperty("tenantId")
    private String tenantId = null;

    @JsonProperty("feeType")
    private String feeType = null;

}