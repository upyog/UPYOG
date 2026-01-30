package org.egov.vehicle.trip.web.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Validated
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-01-06T05:34:12.238Z[GMT]")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class VehicleTripRequest {

	@NotNull
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	//	@NotNull
	//	@Valid
	//	@JsonProperty("vehicleTrip")
	//	private VehicleTrip vehicleTrip;
	
	@NotNull
	@NotEmpty
	@Valid
	@JsonProperty("vehicleTrip")
	private List<VehicleTrip> vehicleTrip;

	
	
	@JsonProperty("workflow") 
	private Workflow workflow;
	 

}
