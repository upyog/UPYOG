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
public class WMSFunctionApplication {
	
	@JsonProperty("function_id")
	private String functionId = null;
	
	@JsonProperty("function_name")
    private String functionName = null;

	@JsonProperty("function_code")
	 private String functionCode = null;
	
	@JsonProperty("function_level")
	 private Integer functionLevel = null;
	
	
	
	@JsonProperty("status")
	 private String status = null;
	
	
	 
	 @JsonProperty("tenantId")
	 private String tenantId = null;
	 
	 @JsonProperty("auditDetails")
	 private AuditDetails auditDetails = null;
	 
	  
}
