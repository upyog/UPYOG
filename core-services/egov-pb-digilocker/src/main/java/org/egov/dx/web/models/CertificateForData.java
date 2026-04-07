package org.egov.dx.web.models;

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

public class CertificateForData {
	
	@XmlAttribute
    @XStreamAlias("dateOfRegistration")
    private String dateOfRegistration;
    
	@XmlAttribute
    @XStreamAlias("subRegistrarOffice")
    private String subRegistrarOffice;
	
	@XmlAttribute
    @XStreamAlias("subRegistrarName")
    private String subRegistrarName;
    
}
