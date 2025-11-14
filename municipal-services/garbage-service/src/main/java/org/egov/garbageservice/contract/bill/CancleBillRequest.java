package org.egov.garbageservice.contract.bill;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	private String tenantId;
	
	@NotNull
	@Valid
	private String reason;
}
