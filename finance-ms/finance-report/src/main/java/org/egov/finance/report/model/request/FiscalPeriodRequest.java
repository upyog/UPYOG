package org.egov.finance.report.model.request;

import org.egov.finance.report.model.FiscalPeriodModel;
import org.egov.finance.report.model.RequestInfo;

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
