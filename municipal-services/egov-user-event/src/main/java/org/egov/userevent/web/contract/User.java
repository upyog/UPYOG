package org.egov.userevent.web.contract;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class User {
	
	@JsonProperty("id")
    private Long id;
	
	  private Set<Role> roles;

	    private String uuid;
	    private String userName;
	    private String name;
	    private String mobileNumber;
	    private String emailId;
	    private String locale;
	    private String type;
	 
	    private boolean active;
	    private String tenantId;
	    private String permanentCity;
}
