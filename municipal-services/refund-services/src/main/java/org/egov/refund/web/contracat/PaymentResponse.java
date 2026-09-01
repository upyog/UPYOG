package org.egov.refund.web.contracat;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("Payments")
    private List<Payment> payments;

}
