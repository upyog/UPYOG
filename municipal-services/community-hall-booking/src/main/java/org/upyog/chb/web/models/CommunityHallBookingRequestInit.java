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
public class CommunityHallBookingRequestInit {
	@JsonProperty("bookingId")
	private String bookingId = null;

	@JsonProperty("tenantId")
	private String tenantId = null;

	@JsonProperty("communityHallId")
	private Integer communityHallId = null;

	@JsonProperty("bookingStatus")
	private String bookingStatus;

	private Object bookingDetails;

	@JsonProperty("createdBy")
	private String createdBy = null;

	@JsonProperty("createdDate")
	private Long createdDate = null;

	@JsonProperty("lastModifiedBy")
	private String lastModifiedBy = null;

	@JsonProperty("lastModifiedDate")
	private Long  lastModifiedDate = null;

}