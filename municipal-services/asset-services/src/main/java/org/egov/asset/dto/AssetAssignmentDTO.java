package org.egov.asset.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AssetAssignmentDTO {

	@JsonProperty("assignmentId")
	private String assignmentId ;
	
    
    @JsonProperty("assignedUserName")
    private String assignedUserName ;
    
    @JsonProperty("employeeCode")
    private String employeeCode ;
    
    @JsonProperty("designation")
    private String designation;
    
    @JsonProperty("department")
    private String department;
    
    @JsonProperty("assignedDate")
    private Long assignedDate;
    
    @JsonProperty("isAssigned")
    private Boolean isAssigned;

    @JsonProperty("returnDate")
    private Long returnDate;
}
