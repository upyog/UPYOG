package org.egov.pt.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OwnerInfoExternal {

	@JsonProperty("name")
    private String name;
	
	@JsonProperty("mobileNumber")
    private String mobileNumber;
	
	 @JsonProperty("emailId")
     private String emailId;
	 
	 @JsonProperty("active")
     private Boolean active;
}
