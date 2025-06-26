package org.egov.finance.inbox.model.request;

import org.egov.finance.inbox.model.FiscalPeriodModel;
import org.egov.finance.inbox.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class FiscalPeriodRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("FiscalPeriod")
	private FiscalPeriodModel fiscalPeriod;

}
