package org.egov.asset.web.models;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Representation of a asset assignment . Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. 
 */
@ApiModel(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetAssignment {
	
	@JsonProperty("assignmentId")
    private String assignmentId ;
	
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
