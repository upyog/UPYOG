package org.upyog.adv.web.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Details for new booking of advertisement
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequest {
	
	@Valid
	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

	@Valid
	@JsonProperty("bookingApplication")
	private  BookingDetail bookingApplication; 

	@JsonProperty("isDraftApplication")
	private boolean isDraftApplication;
}
