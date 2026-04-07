package org.egov.dx.web.models;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

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

@XStreamAlias("Certificate")
public class Certificate {
	 @XStreamAsAttribute
	    private String signature;

	    @XStreamAsAttribute
	    private String language;
	    
	    @XStreamAsAttribute
	    private String name;
	    
	    @XStreamAsAttribute
	    private String type;
	    
	    @XStreamAsAttribute
	    private String number;
	    
	    @XStreamAsAttribute
	    private String prevNumber;
	    

	    @XStreamAsAttribute
	    private String expiryDate;
	    

	    @XStreamAsAttribute
	    private String validFromDate;
	    
	    @XStreamAsAttribute
	    private String issuedAt;
	    

	    @XStreamAsAttribute
	    private String issueDate;
	    
	    
	    @XStreamAsAttribute
	    private String status;
	    
	    public String getname() {
	        return name;
	    }

	    public void setname(String name) {
	        this.name = name;
	    }

	   
	    
	@XStreamAlias("IssuedBy")
    private IssuedBy IssuedBy;
	
    @XStreamAlias("IssuedTo")
    private IssuedTo IssuedTo;
	
	@XStreamAlias("CertificateData")
    private CertificateData certificateData;
	
   
  
    
}
