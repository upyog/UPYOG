package org.egov.advertisementcanopy.model;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteBookingRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("SiteBookings")
	private List<SiteBooking> siteBookings;

}
