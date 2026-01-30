package org.upyog.adv.web.models;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

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


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = { "addType", "location", "faceArea","nightLight", "bookingDate"})
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

//	private String fromTime;
//
//	private String toTime;

	private String tenantId;

	@JsonProperty("slotStaus")
	private String slotStaus;
	
	private String uuid;
	
	public Boolean isNightLight() {
	        return nightLight;
	    }
	
	


}
