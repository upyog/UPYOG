package org.egov.finance.report.model.request;

import org.egov.finance.report.model.AccountDetailModel;
import org.egov.finance.report.model.RequestInfo;

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
