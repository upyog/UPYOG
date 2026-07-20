
package org.egov.garbageservice.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.contract.bill.Demand;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

/**
 * Request to generate arrear demands for garbage accounts via billing service.
 * Wraps RequestInfo and a list of contract Demand objects (class name retains legacy spelling).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenrateArrearRequest {

	@NotNull
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@Valid
	@NotNull
	@Default
	@JsonProperty("Demands")
	private List<Demand> demands = new ArrayList<>();
}
