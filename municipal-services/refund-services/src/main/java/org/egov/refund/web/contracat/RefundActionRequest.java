package org.egov.refund.web.contracat;

import java.util.Map;

import org.egov.common.contract.request.RequestInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundActionRequest {

	private RequestInfo requestInfo;

	private String action;

	private String userId;

	private String remarks;

	private Map<String, Object> additionalDetails;
}