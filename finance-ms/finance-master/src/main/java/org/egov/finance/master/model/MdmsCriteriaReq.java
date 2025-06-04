package org.egov.finance.master.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MdmsCriteriaReq {
	

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	

	@JsonProperty("MdmsCriteria")
	private MdmsCriteria mdmsCriteria;
	
}
