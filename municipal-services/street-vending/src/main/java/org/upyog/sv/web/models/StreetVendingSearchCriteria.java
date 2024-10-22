package org.upyog.sv.web.models;

import java.util.List;

import org.upyog.sv.validator.ValidDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class StreetVendingSearchCriteria {
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("bookingIds")
	private List<String> bookingIds;

	@JsonProperty("status")
	private String status;
	
	private String communityHallCode;

	@JsonProperty("bookingNo")
	private String bookingNo;

	@JsonProperty("mobileNumber")
    private String mobileNumber;
	
	@JsonProperty("offset")
	private Integer offset;

	@JsonProperty("limit")
	private Integer limit;

	@ValidDate
	@JsonProperty("fromDate")
	private String fromDate;

	@ValidDate
	@JsonProperty("toDate")
	private String toDate;
	
	private boolean isCountCall;

	@JsonProperty("createdBy")
	@JsonIgnore
	private List<String> createdBy;
	
	public boolean isEmpty() {
		return (this.tenantId == null && this.status == null && this.bookingIds == null && this.bookingNo == null
				&& this.mobileNumber == null 
				//&& this.offset == null && this.limit == null
				&& this.fromDate == null && this.toDate == null && this.createdBy == null);
	}

	public boolean tenantIdOnly() {
		return (this.tenantId != null && this.status == null && this.bookingIds == null && this.bookingNo == null
				&& this.mobileNumber == null 
				//&& this.offset == null && this.limit == null
				&& this.fromDate == null && this.toDate == null && this.createdBy == null);
	}
}
