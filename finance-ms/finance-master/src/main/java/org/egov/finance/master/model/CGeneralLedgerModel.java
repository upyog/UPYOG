package org.egov.finance.master.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class CGeneralLedgerModel {
	private Long id;
	private Integer voucherlineId;
	private Date effectiveDate;
	private Long glcodeId;
	private String glcode;
	private Double debitAmount;
	private Double creditAmount;
	private String description;
	private Long voucherHeaderId;
	private Integer functionId;
	private Boolean isSubLedger;
	private List<CGeneralLedgerDetailModel> generalLedgerDetails;

}
