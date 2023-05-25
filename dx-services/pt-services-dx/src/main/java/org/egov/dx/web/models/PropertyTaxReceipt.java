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
@XStreamAlias("PropertyTaxReceipt")

public class PropertyTaxReceipt {
	
	@XmlAttribute
    @XStreamAlias("servicetype")
    private String servicetype;
    
	@XmlAttribute
    @XStreamAlias("receiptNo")
    private String receiptNo;
	
	@XmlAttribute
    @XStreamAlias("propertyID")
    private String propertyID;
    
	@XmlAttribute
    @XStreamAlias("paymentDate")
    private String paymentDate;
    
	@XmlAttribute
    @XStreamAlias("ownerName")
    private String ownerName;
	
	@XmlAttribute
    @XStreamAlias("ownerContact")
    private String ownerContact;
	
	@XmlAttribute
    @XStreamAlias("paymentstatus")
    private String paymentstatus;
	
	@XStreamAlias("Payment")
    private PaymentForReceipt paymentForReceipt;
	
}
