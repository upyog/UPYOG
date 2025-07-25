package org.egov.finance.report.model.request;

import org.egov.finance.report.model.BankbranchModel;
import org.egov.finance.report.model.RequestInfo;

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
