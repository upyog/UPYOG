package org.egov.refund.web.contracat;



import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentWorkflowRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("PaymentWorkflows")
    @Size(min = 1)
    @Valid
    private List<PaymentWorkflow> paymentWorkflows;


}
