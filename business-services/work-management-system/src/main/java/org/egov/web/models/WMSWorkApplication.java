package org.egov.web.models;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class WMSWorkApplication {
	
	@JsonProperty("work_id")
	private String workId = null;
	
	@JsonProperty("project_id")
    private Integer projectId = null;

	@JsonProperty("work_no")
	 private String workNo = null;
	
	@JsonProperty("work_name")
	 private String workName = null;
	
	@JsonProperty("project_name")
	 private String projectName = null;
	
	@JsonProperty("department_name")
	 private String departmentName = null;
	
	@JsonProperty("work_type")
	 private String workType = null;
	
	@JsonProperty("work_category")
	 private String workCategory = null;
	
	@JsonProperty("work_subtype")
	 private String workSubtype = null;
	 
	 @JsonProperty("project_phase")
	 private String projectPhase = null;
	 
	 @JsonProperty("deviation_percent")
	 private Integer deviationPercent = null;
	 
	 @JsonProperty("start_location")
	 private String startLocation = null;
	 
	 @JsonProperty("end_location")
	 private String endLocation = null;
	 
	 @JsonProperty("financial_year")
	 private String financialYear = null;
	 
	 @JsonProperty("budget_head")
	 private String budgetHead = null;
	 
	 @JsonProperty("tenantId")
	 private String tenantId = null;
	 
	 @JsonProperty("auditDetails")
	 private AuditDetails auditDetails = null;
	 
	  @ManyToOne(fetch = FetchType.LAZY, optional = false)
	  @JoinColumn(name = "project_Id", nullable = false)
	  @JsonIgnore
	  private Project project;
}
