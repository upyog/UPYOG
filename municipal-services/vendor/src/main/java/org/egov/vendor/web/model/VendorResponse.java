package org.egov.vendor.web.model;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response of Vendor detail
 */
//@Schema(description = "Response of Vendor detail")
@Validated
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-01-06T05:34:12.238Z[GMT]")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class VendorResponse {

	@JsonProperty("responseInfo")
	private ResponseInfo responseInfo = null;

	//@JsonProperty("vendor")
	//private Vendor vendor = null;
	
	 @JsonProperty("vendor")
	 @Valid
	 private List<Vendor> vendor = null;
	 
	 @JsonProperty("totalCount")
	 private Integer totalCount = null;
	 
	 

}
