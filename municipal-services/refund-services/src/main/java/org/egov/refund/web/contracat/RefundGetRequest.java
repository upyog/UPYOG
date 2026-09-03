package org.egov.refund.web.contracat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundGetRequest {

    private RequestInfo RequestInfo;

    private String tenantId;

    private String id;
    
    private String refundNo;

}