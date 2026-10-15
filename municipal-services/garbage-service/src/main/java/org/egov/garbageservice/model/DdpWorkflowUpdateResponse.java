package org.egov.garbageservice.model;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DdpWorkflowUpdateResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	private String uuid;

	private String vendorPrintVerified;

	private Boolean ulbVerified;

	private Boolean installationDone;

	private String ddpLatitude;

	private String ddpLongitude;

	private Boolean ddpPrintingDone;

	private Boolean ddpDispatched;

	private String ddpRejectionReason;

	private String remarks;
}
