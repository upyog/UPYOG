package org.upyog.sv.web.models;

import jakarta.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Schema(description = "Contract class to receive request. Create str")
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class StreetVendingRequest {

	@Valid
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("streetVendingDetail")
	private StreetVendingDetail streetVendingDetail;
	
	private boolean isDraftApplication;

}
