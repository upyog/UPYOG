package org.upyog.chb.web.models;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Details for new booking of community halls
 */
@ApiModel(description = "Request object for creating new booking of community halls")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityHallBookingRequest {
	
	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
	
	@JsonProperty("hallsBookingApplication")
	private CommunityHallBookingDetail hallsBookingApplication; 
	
}
