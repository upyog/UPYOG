package org.egov.finance.inbox.model.request;

import org.egov.finance.inbox.model.BankbranchModel;
import org.egov.finance.inbox.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class BankbranchRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("bankbranch")
	private BankbranchModel bankbranch;

}
