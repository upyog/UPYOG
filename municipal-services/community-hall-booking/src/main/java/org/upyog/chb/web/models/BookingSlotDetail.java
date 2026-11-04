package org.upyog.chb.web.models;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.upyog.chb.util.CommunityHallBookingUtil;

import com.fasterxml.jackson.annotation.JsonFormat;
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
	@JsonProperty("unitCode")
	private String unitCode;
	
	@NotBlank
	private String capacity;
	
	@NotNull
	@JsonFormat(pattern = CommunityHallBookingUtil.DATE_FORMAT)
	private LocalDate bookingDate;
	
	@NotNull
	@JsonFormat(pattern = "HH:mm")
	private LocalTime bookingFromTime;
	
	@NotNull
	@JsonFormat(pattern = "HH:mm")
	private LocalTime bookingToTime;
	
	@JsonProperty("status")
	private String status = null;
	
	private AuditDetails auditDetails;
	
}
