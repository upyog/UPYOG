package org.egov.pt.models;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertySearchResponse {
	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo = null;

	private List<PropertyBookingDetail> applicationDetails;

}
