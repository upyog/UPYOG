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
public class WMSDepartmentApplication {
	
	@JsonProperty("dept_id")
	private String deptId = null;
	
	@JsonProperty("dept_name")
    private String deptName = null;

	@JsonProperty("dept_status")
	 private String deptStatus = null;
	
	
	@JsonProperty("tenantId")
	 private String tenantId = null;
	 
	 @JsonProperty("auditDetails")
	 private AuditDetails auditDetails = null;
	 
	  
}
