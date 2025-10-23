package org.egov.finance.report.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class CVoucherHeaderModel {

	private Long id;
	private String name;
	private String type;
	private String description;
	private Date effectiveDate;
	private String voucherNumber;
	private Date voucherDate;
	private Long fundId;
	private Integer fiscalPeriodId;
	private Integer status;
	private Long originalvcId;
	private Integer isConfirmed;
	private Long refvhId;
	private String cgvn;
	private Integer moduleId;

	// Transient model fields
	private String voucherSubType;
	private Boolean isRestrictedtoOneFunctionCenter;
	private String voucherNumberPrefix;
	private List<Long> accountDetailIds;
	private List<Long> subLedgerDetailIds;
	private String partyName;
	private String partyBillNumber;
	private Date partyBillDate;
	private String billNumber;
	private String departmentName;
	private Date billDate;
	private Long approvalDepartment;
	private String approvalComent;
	private String voucherNumType;
	private String fiscalName;

}
