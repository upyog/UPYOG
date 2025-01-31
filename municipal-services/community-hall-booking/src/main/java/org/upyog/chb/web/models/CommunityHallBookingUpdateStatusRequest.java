package org.upyog.chb.web.models;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.chb.web.models.workflow.ProcessInstance;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommunityHallBookingUpdateStatusRequest {

	@Valid
	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
	
	private String bookingNo;	

 	private ProcessInstance workflow;


    
}
