package org.upyog.sv.web.models.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

<<<<<<< HEAD
import jakarta.validation.Valid;
=======
import javax.validation.Valid;
>>>>>>> master-LTS

/**
 * The payment object, containing all necessary information for initiating a payment and the request body metadata
 */
@Validated
<<<<<<< HEAD
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-06-05T12:58:12.679+05:30")
=======
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-06-05T12:58:12.679+05:30")
>>>>>>> master-LTS

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

