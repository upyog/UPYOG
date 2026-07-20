package org.upyog.adv.web.models.transaction;

import jakarta.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;


/**
 * The payment object, containing all necessary information for initiating a payment and the request body metadata
 */
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-06-05T12:58:12.679+05:30")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {

    @JsonProperty("RequestInfo")
    @Valid
    private RequestInfo requestInfo;

    @JsonProperty("Transaction")
    @Valid
    private Transaction transaction;


}

