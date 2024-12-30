package org.egov.asset.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.asset.dto.AssetDTO;
import org.egov.common.contract.response.ResponseInfo;

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
public class AssetUpdationResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("Assets")
	@Valid
	private List<AssetUpdate> assets = null;
}
