package org.egov.finance.master.model.request;

import org.egov.finance.master.model.ChartOfAccountsModel;
import org.egov.finance.master.model.RequestInfo;

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
