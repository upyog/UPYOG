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
@XStreamAlias("CertificateData")

public class CertificateData {
	
	
    @XStreamAlias("Certificate")
    private CertificateForData certificate;
    
    @XStreamAlias("PropertyTaxReceipt")
    private PropertyTaxReceipt propertyTaxReceipt;
    
}
