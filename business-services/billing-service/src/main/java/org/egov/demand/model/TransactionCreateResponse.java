package org.egov.demand.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import org.egov.common.contract.response.ResponseInfo;


import javax.validation.Valid;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionCreateResponse {

    @JsonProperty("ResponseInfo")
    @Valid
    private ResponseInfo responseInfo;

    @JsonProperty("Transaction")
    @Valid
    private Transaction transaction;
}
