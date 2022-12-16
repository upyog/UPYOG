package org.egov.dx.web.models;

import javax.validation.constraints.Size;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@XStreamAlias("DocDetails")

public class DocDetails {
	
	@XStreamAlias("DocType")
    private String docType;
	
	@XStreamAlias("FullName")
    private String fullName;
	
	@XStreamAlias("DOB")
    private String dob;
	
	@XStreamAlias("GENDER")
    private String gender;
	
	@XStreamAlias("DigiLockerId")
    private String digiLockerId;
	
	@XStreamAlias("Mobile")
    private String mobile;
	
    @Size(max=64)
    @XStreamAlias("PropertyID")
    private String propertyId;

    @XStreamAlias("City")
    private String city;
     
}
