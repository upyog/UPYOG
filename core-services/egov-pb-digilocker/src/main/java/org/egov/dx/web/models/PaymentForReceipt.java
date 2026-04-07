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
@XStreamAlias("Payment")

public class PaymentForReceipt {
	
	@XmlAttribute
    @XStreamAlias("paymentMode")
    private String paymentMode;
    
	@XmlAttribute
    @XStreamAlias("billingPeriod")
    private String billingPeriod;
	
	@XmlAttribute
    @XStreamAlias("propertyTaxAmount")
    private String propertyTaxAmount;
    
	@XmlAttribute
    @XStreamAlias("paidAmount")
    private String paidAmount;
    
	@XmlAttribute
    @XStreamAlias("pendingAmount")
    private String pendingAmount;
	
	@XmlAttribute
    @XStreamAlias("excessAmount")
    private String excessAmount;
	
	@XmlAttribute
    @XStreamAlias("transactionID")
    private String transactionID;
	
	@XmlAttribute
    @XStreamAlias("g8ReceiptNo")
    private String g8ReceiptNo;
	
	@XmlAttribute
    @XStreamAlias("g8ReceiptDate")
    private String g8ReceiptDate;
	
	@XStreamAlias("Payment")
    private PaymentForReceipt paymentForReceipt;
	
}
