package org.upyog.chb.web.models;

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

	private String communityHallId;

	private String communityHallCode;

	private String communityHallName;

	private String hallCodeId;

	private String hallCodeName;

	private String hallCode;

	private String bookingStartDate;

	private String bookingEndDate;

	// This flag will be true if multiple slots are available in hall/park
	// for same day
	private boolean isMultipleSlotsAvaialable;

	private String fromTime;

	private String toTime;

}
