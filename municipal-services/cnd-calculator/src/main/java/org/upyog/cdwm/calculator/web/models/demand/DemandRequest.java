package org.upyog.cdwm.calculator.web.models.demand;

import java.util.ArrayList;
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
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandRequest {
	
	@NotNull
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@Valid
	@NotNull
	@JsonProperty("Demands")
	private List<Demand> demands = new ArrayList<>();
}
