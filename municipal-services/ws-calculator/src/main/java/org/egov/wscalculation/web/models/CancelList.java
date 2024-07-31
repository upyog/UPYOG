package org.egov.wscalculation.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.coyote.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class CancelList {


	
	
	
	
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


