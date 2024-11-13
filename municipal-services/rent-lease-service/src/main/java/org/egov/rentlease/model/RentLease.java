package org.egov.rentlease.model;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentLease {
	
	private String uuid;
	private String tenantId;
	private AuditDetails auditDetails;
	private String mobileNo;
	private Long startDate;
	private Long endDate;
	private Long months;
	private JsonNode applicantDetails;
	private String assetId;
	private String status;
	private boolean isActive;
	private String applicationNo;
	private String workflowAction;
	private Boolean isOnlyWorkflowCall = false;
	private String comments;
	

}
