package org.egov.finance.report.model.request;

import org.egov.finance.report.model.AccountDetailKeyModel;
import org.egov.finance.report.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AccountDetailKeyRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("accountDetailKey")
	private AccountDetailKeyModel accountDetailKey;

}
