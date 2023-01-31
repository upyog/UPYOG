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
public class PullURIDocRequest {
	
	@XStreamAlias("URI")
    private String URI;
	
	@XStreamAlias("DigiLockerId")
    private String digiLockerId;
	
	@XStreamAlias("UID")
    private String UID;
	
    @Size(max=64)
    @XStreamAlias("FullName")
    private String FullName;

    @XStreamAlias("DOB")
    private String DOB;
    
    @XStreamAlias("GENDER")
    private String gender;
    
    @XStreamAlias("action")
    private String action;
     
}
