package org.egov.demand.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.egov.demand.web.contract.PaymentSearchCriteria;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * The payment object, containing all necessary information for initiating a payment and the request body metadata
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-06-05T12:58:12.679+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectionPaymentCriteriaRequest {

    @JsonProperty("RequestInfo")
    @Valid
    private RequestInfo requestInfo;

    @JsonProperty("paymentcriteria")
    @Valid
    private PaymentSearchCriteria payment;


}

