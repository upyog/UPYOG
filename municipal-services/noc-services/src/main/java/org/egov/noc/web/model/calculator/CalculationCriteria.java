package org.egov.noc.web.model.calculator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import org.egov.noc.web.model.Noc;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalculationCriteria {
	@JsonProperty("NOC")
	private Noc noc = null;

    @JsonProperty("applicationNumber")
    private String applicationNumber = null;

    @JsonProperty("tenantId")
    private String tenantId = null;


}