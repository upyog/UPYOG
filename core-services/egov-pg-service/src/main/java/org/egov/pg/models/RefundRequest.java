package org.egov.pg.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class RefundRequest {

    @JsonProperty("RequestInfo")
    private org.egov.common.contract.request.RequestInfo requestInfo;
    
    @JsonProperty("Refund")
    private Refund refund;
}
