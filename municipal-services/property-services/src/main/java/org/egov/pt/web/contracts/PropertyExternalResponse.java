package org.egov.pt.web.contracts;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.models.PropertyExternal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PropertyExternalResponse {

	@JsonProperty("ResponseInfo")
	  private ResponseInfo responseInfo;

	  @JsonProperty("Properties")
	  private List<PropertyExternal> properties;
}
