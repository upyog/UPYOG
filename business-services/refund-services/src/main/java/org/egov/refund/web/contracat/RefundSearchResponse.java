package org.egov.refund.web.contracat;

import java.util.List;

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
public class RefundSearchResponse {

	private ResponseInfo responseInfo;

	private List<Refund> refunds;

	private Integer totalCount;

}