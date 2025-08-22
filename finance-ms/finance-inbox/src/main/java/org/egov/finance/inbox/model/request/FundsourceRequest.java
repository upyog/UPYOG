package org.egov.finance.inbox.model.request;

import org.egov.finance.inbox.model.FundsourceModel;
import org.egov.finance.inbox.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FundsourceRequest {
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	@JsonProperty("fundsource")
	private FundsourceModel fundsource;

}
