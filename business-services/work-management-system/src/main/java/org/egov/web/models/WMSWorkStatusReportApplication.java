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
public class WMSWorkStatusReportApplication {
	
	@JsonProperty("wsr_id")
	private String wsrId = null;
	
	@JsonProperty("project_name")
    private String projectName = null;

	@JsonProperty("work_name")
	 private String workName = null;
	
	@JsonProperty("activity_name")
	 private String activityName = null;
	
	@JsonProperty("role_name")
	 private String roleName = null;
	
	@JsonProperty("employee_name")
	 private String employeeName = null;
	
	@JsonProperty("start_date")
	 private String startDate = null;
	
	@JsonProperty("end_date")
	 private String endDate = null;
	
	@JsonProperty("remarks_content")
	 private String remarksContent = null;
	
	
	 
	 @JsonProperty("tenantId")
	 private String tenantId = null;
	 
	 @JsonProperty("auditDetails")
	 private AuditDetails auditDetails = null;
	 
	  
}
