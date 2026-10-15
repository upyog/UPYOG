package org.egov.garbageservice.model;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class CustomAmountUpdateRequest {
    private String tenantId;
    private String billId;
    private String demandId;
    private BigDecimal customAmount;
    private String reason;
    
    @JsonProperty("requestInfo")
	private RequestInfo requestInfo;
}
