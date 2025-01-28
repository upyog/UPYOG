package org.upyog.chb.web.models;

import java.util.List;

import javax.validation.Valid;

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
