package org.egov.finance.master.model.request;

import org.egov.finance.master.model.FundsourceModel;
import org.egov.finance.master.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FundsourceRequest {
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	@JsonProperty("fundsource")
	private FundsourceModel fundsource;

}
