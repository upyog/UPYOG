package org.upyog.cdwm.calculator.web.models;

import java.util.List;

<<<<<<< HEAD
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
=======
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
>>>>>>> master-LTS

import org.egov.common.contract.request.RequestInfo;

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
public class CalculationRequest {

	@JsonProperty("RequestInfo")
	@NotNull
	@Valid
	private RequestInfo requestInfo = null;

	@JsonProperty("CalulationCriteria")
	@Valid
	private List<CalulationCriteria> calulationCriteria = null;

}
