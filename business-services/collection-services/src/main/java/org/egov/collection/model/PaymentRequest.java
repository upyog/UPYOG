package org.egov.collection.model;



import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.egov.common.contract.request.RequestInfo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull
    @Valid
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @NotNull
    @Valid
    @JsonProperty("Payment")
    private Payment payment;

}
