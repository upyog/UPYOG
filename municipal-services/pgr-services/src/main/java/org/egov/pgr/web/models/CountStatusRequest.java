package org.egov.pgr.web.models;

import java.util.List;

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
public class CountStatusRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	private List<CountStatusUpdate> countStatusUpdate;

}
