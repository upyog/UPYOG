package org.egov.collection.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReport {

    @JsonProperty("ResponseInfo")
    private BigDecimal totalAmount;

    @JsonProperty("Payments")
    private List<Payment> payments;
    
    
    @JsonProperty("tenantId")
    private String  tenantId;
    

}
