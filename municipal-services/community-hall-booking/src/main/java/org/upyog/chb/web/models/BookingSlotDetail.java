package org.upyog.chb.web.models;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * Keeping slot details separately because it will be treated as booking item in booking
 */
public class BookingSlotDetail {
	
	@JsonProperty("slotId")
	private String slotId = null;
	
	@JsonProperty("bookingId")
	private String bookingId = null;
	
	@NotBlank
	@JsonProperty("hallCode")
	private String hallCode;
	
	private String hallName;
	
	@NotBlank
	private String bookingDate = null;
	
	@NotBlank
	private String bookingFromTime;
	
	@NotBlank
	private String bookingToTime;
	
	@JsonProperty("status")
	private String status = null;
	
	private AuditDetails auditDetails;

}
