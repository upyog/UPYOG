package org.egov.asset.web.models;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetActionResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;
	private List<AssetApplicationDetail> applicationDetails;

	private Map<String, Long> applicationTypesCount;

}
