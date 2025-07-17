package org.egov.finance.inbox.model;

import lombok.Data;

@Data
public class ChequeFormatModel {
	private Long id;
	private String chequeName;
	private String chequeType;
	private Double chequeLength;
	private Double chequeWidth;
	private String accountPayeeCoordinate;
	private String dateFormat;
	private String dateCoordinate;
	private Double payeeNameLength;
	private String payeeNameCoordinate;
	private String amountNumberingFormat;
	private String amountInWordsFirstLineCoordinate;
	private Double amountInWordsFirstLineLength;
	private Double amountInWordsSecondLineLength;
	private String amountInWordsSecondLineCoordinate;
	private Double amountLength;
	private String amountCoordinate;
	private boolean formatStatus;
}
