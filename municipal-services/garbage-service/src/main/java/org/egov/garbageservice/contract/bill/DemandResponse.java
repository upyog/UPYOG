package org.egov.garbageservice.contract.bill;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for billing/demand service responses after create, update, or search.
 *
 * Behavior:
 * - Carries {@link org.egov.common.contract.response.ResponseInfo} and list of {@link Demand} results.
 * - Parsed from REST JSON in {@link DemandRepository} via {@link com.fasterxml.jackson.databind.ObjectMapper}.
 *
 * Notes:
 * - JSON property names use PascalCase ({@code ResponseInfo}, {@code Demands}).
 * - demands defaults to empty list when not returned by the API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("Demands")
	private List<Demand> demands = new ArrayList<>();

}
