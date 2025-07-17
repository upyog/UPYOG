package org.egov.finance.master.model.request;

import org.egov.finance.master.model.BankaccountModel;
import org.egov.finance.master.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class BankaccountRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("Bankaccount")
	private BankaccountModel bankaccount;

}
