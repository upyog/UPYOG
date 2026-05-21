package org.upyog.adv.web.models;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Details for new booking of advertisement 
 */


@ApiModel(description = "Details for slot availabiltity of advertisement booking")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = { "addType", "location", "faceArea", "nightLight", "bookingDate", "advertisementId" })
public class AdvertisementSlotAvailabilityDetail {

	private String addType;
	
	private String location;
	
	private String faceArea;
	
	private Boolean nightLight;
	
	private String bookingId;
	
	private long timerValue;
	
//	private long remainingTimerValue;
	
	private String bookingDate;
	
	private String bookingStartDate;
	
	private String bookingEndDate;
	private String advertisementId;

	private String bookingFromTime;

	private String bookingToTime;

	private String tenantId;

	@JsonProperty("slotStaus")
	private String slotStaus;
	
	private String uuid;
	
	// Additional advertisement details
	private Double amount;
	
	private String advertisementName;
	
	private Integer poleNo;
	
	private String imageSrc;
	
	private Integer width;
	
	private Integer height;
	
	private String lightType;
	
	public Boolean isNightLight() {
	        return nightLight;
	    }
	
	


}
