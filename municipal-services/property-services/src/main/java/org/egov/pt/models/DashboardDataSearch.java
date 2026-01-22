package org.egov.pt.models;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class DashboardDataSearch {
	
	private String tenantid;
	private String ward;
	private String fromDate;
	private String toDate;
	@NotNull
	private String status;

}
