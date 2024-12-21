package org.egov.asset.calculator.web.models;

import java.util.List;
import org.egov.asset.calculator.utils.CalculatorConstants;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

//	@JsonProperty("fsm")
//	private FSM fsm = null;

	@NotNull
	@JsonProperty("tenantId")
	@Size(min = 2, max = 256)
	private String tenantId = null;
	
	@JsonProperty("feeType")
	String Message =  CalculatorConstants.SUCCESS_MESSAGE;
}
