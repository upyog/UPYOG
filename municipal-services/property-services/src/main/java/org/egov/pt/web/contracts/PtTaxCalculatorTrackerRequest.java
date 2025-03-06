package org.egov.pt.web.contracts;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.PtTaxCalculatorTracker;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PtTaxCalculatorTrackerRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("ptTaxCalculatorTracker")
	private PtTaxCalculatorTracker ptTaxCalculatorTracker;
}
