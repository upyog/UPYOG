package org.egov.pt.models;

import lombok.Data;

@Data
public class DashboardDataSearch {
	
	private String tenantid;
	private String ward;
	private String fromDate;
	private String toDate;

}
