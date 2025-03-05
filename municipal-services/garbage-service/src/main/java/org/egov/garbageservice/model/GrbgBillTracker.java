package org.egov.garbageservice.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrbgBillTracker {

	private String uuid;
	private String grbgApplicationId;
	private String tenantId;
	private String month;
	private String year;
	private String fromDate;
	private String toDate;
	private BigDecimal grbgBillAmount;
	private AuditDetails auditDetails;
}
