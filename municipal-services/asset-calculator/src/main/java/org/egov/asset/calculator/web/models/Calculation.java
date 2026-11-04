package org.egov.asset.calculator.web.models;

import org.egov.asset.calculator.utils.CalculatorConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Calculation {


	@JsonProperty("applicationNumber")
	private String applicationNumber = null;

	@NotNull
	@JsonProperty("tenantId")
	@Size(min = 2, max = 256)
	private String tenantId = null;
	
	@JsonProperty("feeType")
	private String feeType = CalculatorConstants.SUCCESS_MESSAGE;
}
