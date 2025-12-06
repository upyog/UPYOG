package org.upyog.sv.web.models;

<<<<<<< HEAD
import jakarta.validation.Valid;
=======
import javax.validation.Valid;
>>>>>>> master-LTS

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


<<<<<<< HEAD
@Schema(description = "Contract class to receive request. Create str")
=======
@ApiModel(description = "Contract class to receive request. Create str")
>>>>>>> master-LTS
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
