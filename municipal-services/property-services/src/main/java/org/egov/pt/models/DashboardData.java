package org.egov.pt.models;

import lombok.Data;

@Data
public class DashboardData {
	
	private String ulb;
	private String fromdate;
	private String todate;
	private String ward;
	private String state;
	private String module;
	private String region;
	private Services services;
	private Revenue revenue;

}
