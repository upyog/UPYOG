package org.egov.finance.master.model.request;

import org.egov.finance.master.model.AccountDetailKeyModel;
import org.egov.finance.master.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AccountDetailKeyRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("accountDetailKey")
	private AccountDetailKeyModel accountDetailKey;

}
