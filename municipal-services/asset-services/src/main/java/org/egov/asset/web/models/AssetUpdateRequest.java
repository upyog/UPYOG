package org.egov.asset.web.models;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AssetUpdateRequest {
	
  @JsonProperty("RequestInfo")
  private RequestInfo requestInfo;
	
	@JsonProperty("Asset")
	@Valid
	private List<AssetUpdate> assetUpdate ;

}
