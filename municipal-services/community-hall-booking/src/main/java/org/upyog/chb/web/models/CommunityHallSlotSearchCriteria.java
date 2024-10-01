package org.upyog.chb.web.models;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.upyog.chb.validator.ValidDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommunityHallSlotSearchCriteria {
	@JsonProperty("tenantId")
	private String tenantId;

	@NotBlank
	private String communityHallCode;

	private String hallCode;
	
	private List<String> hallCodes;

	@NotBlank
	@ValidDate
	private String bookingStartDate;

	@NotBlank
	@ValidDate
	private String bookingEndDate;

	// This flag will be true if multiple slots are available in hall/park
	// for same day
	private boolean isMultipleSlotsAvaialable;

	private String fromTime;

	private String toTime;

}
