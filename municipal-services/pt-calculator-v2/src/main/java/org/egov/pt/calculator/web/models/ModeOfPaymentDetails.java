package org.egov.pt.calculator.web.models;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ModeOfPaymentDetails {
	
	private String paymentMode;
	
	private Long formDate;
	
	private Long toDate;
	
	private BigDecimal taxAmount;
	
	private BigDecimal pastAmount;

}
