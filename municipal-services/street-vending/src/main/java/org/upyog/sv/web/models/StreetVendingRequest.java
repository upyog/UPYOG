package org.upyog.sv.web.models;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@ApiModel(description = "Contract class to receive request. Create str")
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class StreetVendingRequest {

	@Valid
	private RequestInfo requestInfo;

	@Valid
	private StreetVendingDetail streetVendingDetail;

}
