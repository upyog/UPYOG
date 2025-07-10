package org.egov.finance.master.model.request;

import org.egov.finance.master.model.AccountDetailTypeModel;
import org.egov.finance.master.model.RequestInfo;

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
