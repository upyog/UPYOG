package org.egov.garbageservice.model;

import java.util.ArrayList;
import java.util.List;

import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Core domain model for a garbage user charge account and its application lifecycle.
 * Links property, applicant details, workflow action, nested application/commercial data, bills, and child accounts.
 * Persisted and returned by GarbageAccountService on create, update, search, and status transitions.
 */
@EqualsAndHashCode(exclude = {"id","uuid","garbageId","propertyId","isOnlyWorkflowCall","workflowAction","workflowComment","grbgApplication","grbgCommercialDetails","auditDetails","garbageBills","childGarbageAccounts"})
public class GarbageAccount {

	private Long id;
	
	@CustomSafeHtml
	private String uuid;

	@CustomSafeHtml
	private String tenantId;

	private Long garbageId;

	@CustomSafeHtml
	private String propertyId;

	@CustomSafeHtml
	private String type;

	@CustomSafeHtml
	private String name;

	@CustomSafeHtml
	private String mobileNumber;

	@CustomSafeHtml
	private String gender;

	@CustomSafeHtml
	private String emailId;

	private Boolean isOwner; 
	
	@CustomSafeHtml
	private String userUuid;

	@CustomSafeHtml
	private String created_by;

	@CustomSafeHtml
	private String declarationUuid;

	@CustomSafeHtml
	private String workflowAction;

	@CustomSafeHtml
	private String workflowComment;
	
	@Builder.Default
	private Boolean isOnlyWorkflowCall = false;

	@CustomSafeHtml
	private String status;

	// new payload field — maps to status after enrichment
	@CustomSafeHtml
	private String applicationStatus;

	private GrbgApplication grbgApplication;

	@CustomSafeHtml
	private String grbgApplicationNumber;

	private GrbgOldDetails grbgOldDetails;

//	private GrbgCommercialDetails grbgCommercialDetails;

	private List<GrbgDocument> documents = new ArrayList<>();

	// new nested payload objects — mapped to flat fields in GarbageAccountService.mapNewPayloadToFlatFields()
	private GarbageSpecification garbageSpecification;

	private PropertyLocation propertyLocation;

	private WorkflowRequest workflow;

	// applicant person details from the new payload; persisted into additionalDetail during enrichment
	private List<ApplicantDetail> applicantDetails = new ArrayList<>();

	private AuditDetails auditDetails;
	
//	private List<GarbageBill> garbageBills;

	private List<GrbgCollectionUnit> grbgCollectionUnits = new ArrayList<>();

	private List<GrbgAddress> addresses = new ArrayList<>();

    private JsonNode additionalDetail = null;

	private List<GarbageAccount> childGarbageAccounts = new ArrayList<>();
	
	@CustomSafeHtml
	private String parentAccount;
	
	private Boolean isActive = false;
	
	private Long subAccountCount;
	
	private Long approvalDate;
	
	@CustomSafeHtml
	private String businessService;
	
	@CustomSafeHtml
	private String channel;

	
}