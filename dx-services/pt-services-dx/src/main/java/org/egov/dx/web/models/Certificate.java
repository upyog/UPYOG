package org.egov.dx.web.models;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;

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
@XStreamAlias("Certificate")
public class Certificate {
	
	@XStreamAlias("IssuedBy")
    private IssuedBy issuedBy;
	
	@XStreamAlias("IssuedTo")
    private IssuedTo issuedTo;
	
	@XStreamAlias("CertificateData")
    private CertificateData certificateData;
	
    @Size(max=64)
    @XStreamAlias("Signature")
    private String signature;

    @XmlAttribute
    @XStreamAlias("language")
    private String language;
    
    @XmlAttribute
    @XStreamAlias("name")
    private String name;
    
    @XmlAttribute
    @XStreamAlias("type")
    private String type;
    
    @XmlAttribute
    @XStreamAlias("number")
    private String number;
    
    @XmlAttribute
    @XStreamAlias("prevNumber")
    private String prevNumber;
    
    @XmlAttribute
    @XStreamAlias("expiryDate")
    private String expiryDate;
    
    @XmlAttribute
    @XStreamAlias("validFromDate")
    private String validFromDate;
    
    @XmlAttribute
    @XStreamAlias("issuedAt")
    private String issuedAt;
    
    @XmlAttribute
    @XStreamAlias("issueDate")
    private String issueDate;
    
    @XmlAttribute
    @XStreamAlias("status")
    private String status;
}
