package org.egov.hrms.model;


import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
public class JurisdictionRole {
	@Size(min=2, max=100)
    private String value;
	
	@Size(min=2, max=100)
    private String label;

}
