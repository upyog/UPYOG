package org.upyog.adv.web.models;

import java.time.LocalDate;
import java.time.LocalTime;

import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.validator.CreateApplicationGroup;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
	private String bookingId = null;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String addType;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String location;

	@NotBlank(groups = CreateApplicationGroup.class)
	private String faceArea;

	@JsonProperty("nightLight")
	private Boolean nightLight;

	@JsonFormat(pattern = BookingUtil.DATE_FORMAT)
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
