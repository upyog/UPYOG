package org.upyog.adv.web.models;

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
import jakarta.validation.Valid;
=======
import javax.validation.Valid;
>>>>>>> master-LTS

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Object holds the advertisement for booking
 */
<<<<<<< HEAD
@Schema(description = "A Object holds the advertisements slot avaialabiltiy details")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
=======
@ApiModel(description = "A Object holds the advertisements slot avaialabiltiy details")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
>>>>>>> master-LTS

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
			this.advertisementSlotAvailabiltityDetails = new ArrayList<AdvertisementSlotAvailabilityDetail>();
		}
		this.advertisementSlotAvailabiltityDetails.add(slotAvailabiltityDetail);
	}
	
	private Integer count;
	
	private String draftId;
	
	private boolean slotBooked;


}

