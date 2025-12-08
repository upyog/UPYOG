package org.egov.finance.report.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CGeneralLedgerDetailModel {
	private Long id;
	private Long generalLedgerId;
	private Integer detailKeyId;
	private Long detailTypeId;
	private BigDecimal amount;
	private String detailKeyName;
}
