package org.egov.finance.report.model.request;

import org.egov.finance.report.model.AccountDetailTypeModel;
import org.egov.finance.report.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class AccountDetailTypeRequest {
	
	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @Valid
    @JsonProperty("AccountDetailType")
    private AccountDetailTypeModel accountDetailType;

}
