package org.egov.pg.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@AllArgsConstructor
@Getter
@ToString
@Builder
public class TransactionRequest {

    @JsonProperty("RequestInfo")
    private org.egov.common.contract.request.RequestInfo requestInfo;

    @JsonProperty("Transaction")
    private Transaction transaction;
}
