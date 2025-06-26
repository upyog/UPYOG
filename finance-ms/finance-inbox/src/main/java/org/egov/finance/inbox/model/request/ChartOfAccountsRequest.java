package org.egov.finance.inbox.model.request;

import org.egov.finance.inbox.model.ChartOfAccountsModel;
import org.egov.finance.inbox.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class ChartOfAccountsRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("chartOfAccount")
	private ChartOfAccountsModel chartOfAccount;

}
