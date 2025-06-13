package org.egov.finance.master.model.request;

import org.egov.finance.master.model.FiscalPeriodModel;
import org.egov.finance.master.model.RequestInfo;

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
