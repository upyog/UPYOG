package org.egov.finance.master.model;

import lombok.Data;

@Data
public class VouchermisModel {
	private Long id;
	private Long fundsourceId;
	private Integer billnumber;
	private Long divisionid;
	private String departmentcode;
	private Long schemeid;
	private Long subschemeid;
	private Integer functionaryId;
	private Long voucherheaderid;
	private Long functionId;
	private String sourcePath;
	private String budgetaryAppnumber;
	private Boolean budgetCheckReq;
	private String referenceDocument;
	private String serviceName;
}
