package org.upyog.adv.web.models;

import jakarta.validation.Valid;
import java.util.List;
import java.util.ArrayList;



import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Object holds the advertisement for booking
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvertisementSlotAvailabilityResponse   {
	
	private ResponseInfo responseInfo;
	
	@JsonProperty("advertisementSlotAvailabiltityDetails")
	@Valid
	private List<AdvertisementSlotAvailabilityDetail> advertisementSlotAvailabiltityDetails; 
	
	public void addNewAdvertisementBookingApplication(AdvertisementSlotAvailabilityDetail slotAvailabiltityDetail) {
		if(this.advertisementSlotAvailabiltityDetails == null) {
			this.advertisementSlotAvailabiltityDetails = new ArrayList<>();
		}
		this.advertisementSlotAvailabiltityDetails.add(slotAvailabiltityDetail);
	}
	
	private Integer count;
	
	private String draftId;
	
	private boolean slotBooked;
	
	private long timerValue;


}

