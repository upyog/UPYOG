package org.upyog.chb.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Object holds the community halls for booking
 */
@ApiModel(description = "A Object holds the community halls slot avaialabiltiy details")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityHallSlotAvailabilityResponse   {
	
	private ResponseInfo responseInfo;
	
	@JsonProperty("hallSlotAvailabiltityDetails")
	@Valid
	private List<CommunityHallSlotAvailabilityDetail> hallSlotAvailabiltityDetails; 
	
	public void addNewHallsBookingApplication(CommunityHallSlotAvailabilityDetail slotAvailabiltityDetail) {
		if(this.hallSlotAvailabiltityDetails == null) {
			this.hallSlotAvailabiltityDetails = new ArrayList<CommunityHallSlotAvailabilityDetail>();
		}
		this.hallSlotAvailabiltityDetails.add(slotAvailabiltityDetail);
	}
	
	private Integer count;

}

