package org.egov.finance.voucher.model.request;

import org.egov.finance.voucher.model.AccountDetailKeyModel;
import org.egov.finance.voucher.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AccountDetailKeyRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("accountDetailKey")
	private AccountDetailKeyModel accountDetailKey;

}
