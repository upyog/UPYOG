package org.egov.finance.report.model.request;

import org.egov.finance.report.model.BankaccountModel;
import org.egov.finance.report.model.RequestInfo;

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
