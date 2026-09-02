package org.egov.refund.web.contracat;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.refund.model.Refund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponse {
	
	 private ResponseInfo responseInfo;
	 private Refund refund; 
}