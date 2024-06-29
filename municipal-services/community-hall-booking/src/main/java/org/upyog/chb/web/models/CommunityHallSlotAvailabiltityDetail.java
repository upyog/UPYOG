package org.upyog.chb.web.models;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Details for new booking of community halls
 */
@ApiModel(description = "Details for slot availabiltity of community halls")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = { "bookingDate" })
public class CommunityHallSlotAvailabiltityDetail {

	private String communityHallCode;

	private String communityHallName;

	private String hallCodeName;

	private String hallCode;

	private String bookingDate;

	private String fromTime;

	private String toTime;

	@JsonProperty("tenantId")
	private String tenantId = null;

	@JsonProperty("slotStaus")
	private String slotStaus;

}
