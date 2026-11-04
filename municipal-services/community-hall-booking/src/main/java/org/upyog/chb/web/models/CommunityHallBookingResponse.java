package org.upyog.chb.web.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Object holds the community halls for booking
 */
@Schema(description = "A Object holds the community halls for booking")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityHallBookingResponse   {
	
	private ResponseInfo responseInfo;
	
	@JsonProperty("hallsBookingApplication")
	@Valid
	private List<VenueBookingDetail> venueBookingApplication; 
	
	public void addNewHallsBookingApplication(VenueBookingDetail bookingDetail) {
		if(this.venueBookingApplication == null) {
			this.venueBookingApplication = new ArrayList<VenueBookingDetail>();
		}
		this.venueBookingApplication.add(bookingDetail);
	}
	
	private Integer count;

}

