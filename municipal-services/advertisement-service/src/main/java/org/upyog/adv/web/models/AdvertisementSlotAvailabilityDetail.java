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
@EqualsAndHashCode(of = { "tenantId", "addType", "location", "faceArea","nightLight", "bookingDate"})
public class AdvertisementSlotAvailabilityDetail {

	private String addType;
	
	private String location;
	
	private String faceArea;
	
	private Boolean nightLight;
	
	private String bookingDate;

//	private String fromTime;
//
//	private String toTime;

	private String tenantId;

	@JsonProperty("slotStaus")
	private String slotStaus;
	
	  public Boolean isNightLight() {
	        return nightLight;
	    }

}
