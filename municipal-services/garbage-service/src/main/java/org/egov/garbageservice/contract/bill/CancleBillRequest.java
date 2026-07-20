package org.egov.garbageservice.contract.bill;

import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.annotations.CustomSafeHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Incoming API request from garbage-service clients to cancel garbage bills.
 *
 * Behavior:
 * - Accepted by {@link org.egov.garbageservice.controller.GarbageBillController#cancelBill(CancleBillRequest)}.
 * - Carries requestInfo, tenantId, consumerCode(s), demandId(s), and cancellation reason.
 * - Processed by GarbageBillService to locate bills and invoke billing-service cancel/update flows.
 *
 * Notes:
 * - Class name uses legacy spelling {@code Cancle} (Cancel); kept for API compatibility.
 * - Mandatory fields validated with {@code @NotNull} and {@code @Valid}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancleBillRequest {

	@NotNull
	private RequestInfo requestInfo;
	
	@NotNull
	@Valid
	private Set<String> consumerCode;
	
	@NotNull
	@Valid
	private Set<String> demandId;

	@NotNull
	@Valid
	@CustomSafeHtml
	private String tenantId;
	
	@NotNull
	@Valid
	@CustomSafeHtml
	private String reason;
}
