package org.upyog.chb.web.models;

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
	
	@JsonProperty("hallCode")
	private String hallCode;
	
	@JsonProperty("bookingSlotDatetime")
	private Long bookingSlotDatetime = null;
	
	@JsonProperty("status")
	private String status = null;
	
	private AuditDetails auditDetails;

}
