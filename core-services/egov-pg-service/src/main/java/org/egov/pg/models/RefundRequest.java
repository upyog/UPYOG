package org.egov.pg.models;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Validated
//@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-06-05T12:58:12.679+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundRequest {

    @JsonProperty("RequestInfo")
    private org.egov.common.contract.request.RequestInfo requestInfo;
    
    @JsonProperty("Refund")
    private Refund refund;
}
