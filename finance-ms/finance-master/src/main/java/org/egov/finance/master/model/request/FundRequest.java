/**
 * Created on Jun 2, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.model.request;

import org.egov.finance.master.model.FundModel;
import org.egov.finance.master.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FundRequest {
	
	@JsonProperty("RequestInfo")
	RequestInfo requestInfo;
	@JsonProperty("Fund")
	FundModel Fund;
	
}

