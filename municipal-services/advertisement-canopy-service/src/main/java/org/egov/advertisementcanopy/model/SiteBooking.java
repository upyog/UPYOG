package org.egov.advertisementcanopy.model;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SiteBooking {
	private String uuid;
	private String applicationNo;
	private String siteUuid;
	private String applicantName;
	private String applicantFatherName;
	private String gender;
	private String mobileNumber;
	private String emailId;
	private String advertisementType;
	private Long fromDate;
	private Integer periodInDays;
	private String hoardingType;
	private String structure;
	private Boolean isOnlyWorkflowCall = false;
	private String action;
	private String comments;
	private String tenantId;
	private Boolean isActive;
	private String status;
	private JsonNode additionalDetail;
	private AuditDetails auditDetails;
}
