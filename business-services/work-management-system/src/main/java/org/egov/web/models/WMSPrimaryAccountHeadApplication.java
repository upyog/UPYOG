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
public class WMSPrimaryAccountHeadApplication {
	
	@JsonProperty("primary_accounthead_id")
	private String primaryAccountHeadId = null;
	
	@JsonProperty("primary_accounthead_name")
    private String primaryAccountHeadName = null;

	@JsonProperty("primary_accounthead_accountno")
	 private String primaryAccountHeadAccountno = null;
	
	@JsonProperty("primary_accounthead_location")
    private String primaryAccountHeadLocation = null;
	@JsonProperty("account_status")
    private String accountStatus = null;
	
	 @JsonProperty("tenantId")
	 private String tenantId = null;
	 
	 @JsonProperty("auditDetails")
	 private AuditDetails auditDetails = null;
	 
	  
}
