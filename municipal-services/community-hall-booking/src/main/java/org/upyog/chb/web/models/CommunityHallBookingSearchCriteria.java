package org.upyog.chb.web.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CommunityHallBookingSearchCriteria {
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("bookingIds")
	private List<String> bookingIds;

	@JsonProperty("status")
	private String status;

	@JsonProperty("applicationNo")
	private String applicationNo;

	@JsonProperty("approvalNo")
	private String approvalNo;

	@JsonProperty("offset")
	private Integer offset;

	@JsonProperty("limit")
	private Integer limit;

	@JsonProperty("approvalDate")
	private Long approvalDate;

	@JsonProperty("fromDate")
	private Long fromDate;

	@JsonProperty("toDate")
	private Long toDate;

	@JsonProperty("createdBy")
	@JsonIgnore
	private List<String> createdBy;

	@JsonProperty("locality")
	private String locality;

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

}
