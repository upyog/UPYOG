package org.upyog.chb.web.models;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.micrometer.core.lang.NonNull;
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
	
	@NotBlank
	private String capacity;
	
	@NonNull
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate bookingDate;
	
	@NonNull
	@JsonFormat(pattern = "HH:mm")
	private LocalTime bookingFromTime;
	
	@NonNull
	@JsonFormat(pattern = "HH:mm")
	private LocalTime bookingToTime;
	
	@JsonProperty("status")
	private String status = null;
	
	private AuditDetails auditDetails;
	
}
