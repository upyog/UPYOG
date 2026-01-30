package org.egov.waterconnection.web.models.collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class PaymentResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @NotNull
    @Valid
    @JsonProperty("Payments")
    private List<Payment> payments;
}
