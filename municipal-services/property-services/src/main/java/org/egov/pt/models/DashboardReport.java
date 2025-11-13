package org.egov.pt.models;

import java.util.List;

import lombok.Data;

@Data
public class DashboardReport {
	
	private String ulb;
	private String fromdate;
	private String todate;
	private String ward;
	private String state;
	private String module;
	private String region;
	private List<ServiceWithProperties> services;
	private List<ServiceWithProperties> revenue;

}
