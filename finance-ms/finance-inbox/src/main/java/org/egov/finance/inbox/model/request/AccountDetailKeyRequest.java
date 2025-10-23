package org.egov.finance.inbox.model.request;

import org.egov.finance.inbox.model.AccountDetailKeyModel;
import org.egov.finance.inbox.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AccountDetailKeyRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("accountDetailKey")
	private AccountDetailKeyModel accountDetailKey;

}
