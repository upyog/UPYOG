package org.egov.pt.web.contracts;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.PropertyExternal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PropertyExternalRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@Valid
	@JsonProperty("Property")
	private PropertyExternal propertyExternal;
}
