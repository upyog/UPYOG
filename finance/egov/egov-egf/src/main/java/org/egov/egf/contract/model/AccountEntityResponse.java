package org.egov.egf.contract.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.egov.infra.microservice.models.ResponseInfo;
import org.egov.masters.model.AccountEntity;

@Getter
@Setter
public class AccountEntityResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("AccountEntity")
    private AccountEntity accountEntity;


}
