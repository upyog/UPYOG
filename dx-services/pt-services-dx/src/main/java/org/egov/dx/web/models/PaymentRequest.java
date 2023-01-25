package org.egov.dx.web.models;


import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull
    @Valid
    @JsonProperty("RequestInfo")
    private RequestInfo RequestInfo;

    @NotNull
    @Valid
    @JsonProperty("Payments")
    private List<Payment> Payments;

}
