package org.egov.waterquality.web.models.collection;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WaterQualityResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo = null;
	
	@JsonProperty("Application")
	private List<WaterQuality> waterQualityApplications;
}
