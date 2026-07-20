package org.egov.garbageservice.contract.bill;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * REST request body for creating or updating demands on the billing/demand service.
 *
 * Behavior:
 * - Wraps {@link org.egov.common.contract.request.RequestInfo} and a list of {@link Demand} records.
 * - Posted by {@link DemandRepository#saveDemand} and {@link DemandRepository#updateDemand}.
 *
 * Notes:
 * - JSON keys use PascalCase ({@code RequestInfo}, {@code Demands}) per eGov billing contract.
 * - demands list defaults to an empty {@link java.util.ArrayList} when built with Lombok builder.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandRequest {

//	@NotNull
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
//	@Valid
//	@NotNull
	@JsonProperty("Demands")
	private List<Demand> demands = new ArrayList<>();
}
