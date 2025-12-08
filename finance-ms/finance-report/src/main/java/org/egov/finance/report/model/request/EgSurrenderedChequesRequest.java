package org.egov.finance.report.model.request;

import org.egov.finance.report.model.EgSurrenderedChequesModel;
import org.egov.finance.report.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class EgSurrenderedChequesRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("egSurrenderedCheque")
	private EgSurrenderedChequesModel egSurrenderedCheque;

}
