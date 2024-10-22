package org.upyog.adv.web.models;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.constraints.NotBlank;

import org.upyog.adv.util.BookingUtil;

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
 * Keeping cart details separately because it will be treated as booking item in booking
 */
public class CartDetail {
	
	@JsonProperty("cartId")
	private String cartId = null;
	
	@JsonProperty("bookingId")
	private String bookingId = null; //foreign key
	
	@NotBlank
	private String addType;
	
	@NotBlank
	private String location;
	
	@NotBlank
	private String faceArea;
	
	@JsonProperty("nightLight")
    private Boolean nightLight;  
	
	
	//@NonNull
	@JsonFormat(pattern = BookingUtil.DATE_FORMAT)
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
