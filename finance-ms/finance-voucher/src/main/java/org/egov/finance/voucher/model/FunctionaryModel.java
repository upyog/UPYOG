package org.egov.finance.voucher.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FunctionaryModel {
	
	   private Integer id;
	    private BigDecimal code;
	    private String name;
	    private Boolean isactive;

}
