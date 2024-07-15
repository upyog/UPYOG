package org.egov.asset.web.models;

import java.util.List;

import org.egov.asset.web.models.workflow.ProcessInstance;

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
public class AssetAssignment {
	
	@JsonProperty("id")
    private String id ;
	
	@JsonProperty("assetApplicaltionNo")
	private String assetApplicaltionNo;

    @JsonProperty("tenantId")
    private String tenantId;
    
    @JsonProperty("assignedUserName")
    private String assignedUserName ;
    
    @JsonProperty("designation")
    private String designation ;
    
    @JsonProperty("department")
    private String department ;
    
    @JsonIgnore
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
    
    @JsonProperty("assignedDate")
    private Long assignedDate;
    
    @JsonProperty("isAssigned")
    private Boolean isAssigned;

    @JsonProperty("returnDate")
    private Long returnDate;

}
