package org.egov.web.models;

import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Project {
	
	@JsonProperty("project_id")
	private Long projectId;
	@JsonProperty("project_number")
	private String projectNumber;
	@JsonProperty("source_of_fund")
	private String sourceOfFund;
	@JsonProperty("scheme_no")
	private String schemeNo;
	@JsonProperty("department")
	private String department;
	/*
	 * @JsonProperty("project_status") private String projectStatus;
	 */
	@JsonProperty("project_name_en")
	private String projectNameEn;
	@JsonProperty("project_name_reg")
	private String projectNameReg;
	@JsonProperty("project_description")
	private String projectDescription;
	@JsonProperty("project_timeline")
	private String projectTimeline;
	@JsonProperty("project_start_date")
	private String projectStartDate;
	@JsonProperty("project_end_date")
	private String projectEndDate;
	@JsonProperty("scheme_name")
	private String schemeName;
	@JsonProperty("approval_number")
	private String approvalNumber;
	@JsonProperty("approval_date")
	private String approvalDate;
	@JsonProperty("status")
	private String status;
	
	
	

}
