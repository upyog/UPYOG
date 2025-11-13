package org.egov.finance.inbox.model.request;

import org.egov.finance.inbox.model.AccountDetailTypeModel;
import org.egov.finance.inbox.model.RequestInfo;

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
