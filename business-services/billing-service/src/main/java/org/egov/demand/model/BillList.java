package org.egov.demand.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BillList {
	

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@JsonProperty("consumerCode")
	private String consumerCode;

	@JsonProperty("businessService")
	private String businessService;

	public String getBusinessService() {
		
		return businessService;
	}

	public String getConsumerCode() {
		
		return consumerCode;
	}
	

}
