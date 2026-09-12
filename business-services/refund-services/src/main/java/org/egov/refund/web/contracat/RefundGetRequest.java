package org.egov.refund.web.contracat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundGetRequest {
	
	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    private String tenantId;

    private String id;
    
    private String refundNo;

}