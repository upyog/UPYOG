package org.upyog.employee.dasboard.web.models;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class EmployeeDashboardDetails {
	
	private Integer applicationReceived;
	
	private Integer applicationPending;
	
	private Integer applicationApproved;
	
	private BigDecimal totalAmount;
	
	private String moduleName;

}
