package org.egov.bpa.web.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class PreapprovedPlanSearchCriteria {

	@JsonProperty("ids")
	private List<String> ids;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("drawingNo")
	private String drawingNo;

	@JsonProperty("plotLength")
	private BigDecimal plotLength;

	@JsonProperty("plotWidth")
	private BigDecimal plotWidth;

	@JsonProperty("roadWidth")
	private BigDecimal roadWidth;
	
	@JsonProperty("plotLengthInFeet")
	private BigDecimal plotLengthInFeet;

	@JsonProperty("plotWidthInFeet")
	private BigDecimal plotWidthInFeet;

  

	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("offset")
	private Integer offset;

	@JsonProperty("limit")
	private Integer limit;

	@JsonProperty("fromDate")
	private Long fromDate;

	@JsonProperty("toDate")
	private Long toDate;

	@JsonIgnore
	private List<String> createdBy;
	
	@JsonProperty("preApprovedCode")
	private String preApprovedCode;

	public boolean isEmpty() {
		return (this.ids == null && this.tenantId == null && this.drawingNo == null && this.plotLength == null
				&& this.plotWidth == null && this.roadWidth == null && this.active == null && this.fromDate == null
				&& this.toDate == null && this.createdBy == null);
	}
}
