package org.egov.refund.web.contracat;

import org.egov.common.contract.request.RequestInfo;
import org.egov.refund.model.Refund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {

	private RequestInfo RequestInfo;

	private Refund refund;
	
}