package org.egov.finance.voucher.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class FundsourceModel {

	private Long id;
	private String code;
	private String name;
	private String type;
	private Long parentId;
	private BigDecimal llevel;
	private Boolean isactive;
	private Boolean isnotleaf;
	private Long finInstId;
	private String fundingType;
	private Double loanPercentage;
	private BigDecimal sourceAmount;
	private Double rateOfIntrest;
	private Double loanPeriod;
	private Double moratoriumPeriod;
	private String repaymentFrequency;
	private Integer noOfInstallment;
	private Long bankAccountId;
	private String govtOrder;
	private Date govtDate;
	private String dpCodeNum;
	private String dpCodeResistration;
	private String finInstLetterNum;
	private Date finInstLetterDate;
	private String finInstSchmNum;
	private Date finInstSchmDate;
	private Long subSchemeId;

}
