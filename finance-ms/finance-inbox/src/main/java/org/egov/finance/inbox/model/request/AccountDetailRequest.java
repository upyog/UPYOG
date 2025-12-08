package org.egov.finance.inbox.model.request;

import org.egov.finance.inbox.model.AccountDetailModel;
import org.egov.finance.inbox.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class AccountDetailRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("AccountDetail")
	private AccountDetailModel accountDetail;

}
