package org.egov.refund.web.contracat;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundSearchResponse {

	private ResponseInfo responseInfo;

	private List<RefundResponse> refunds;

	private Integer totalCount;

}