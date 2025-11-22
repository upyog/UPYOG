package org.egov.pt.web.contracts;

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


public class CancelPropertyBillRequest {
	
	@NotNull
	private RequestInfo requestInfo;
	
	@NotNull
	private Set<String> consumerCode;
	
	@NotNull
	private String businessService;

	@NotNull
	private String tenantId;
	
	@NotNull
	private String reason;
	
	@NotNull
	private String billId;

}
