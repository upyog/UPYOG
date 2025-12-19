package org.egov.pt.models;

import java.util.List;

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
@EqualsAndHashCode(exclude = {"id","uuid","garbageId","propertyId","isOnlyWorkflowCall","workflowAction","workflowComment","grbgApplication","grbgCommercialDetails","auditDetails","garbageBills","childGarbageAccounts"})
public class PropertyAccount {

	private Long id;
	
	private String uuid;

	private String tenantId;

	private Long garbageId;

	private String propertyId;

	private String type;

	private String name;

	private String mobileNumber;

	private String gender;

	private String emailId;

	private Boolean isOwner; 
	
	private String userUuid;

	private String created_by;

	private String declarationUuid;

	private String workflowAction;

	private String workflowComment;
	
	@Builder.Default
	private Boolean isOnlyWorkflowCall = false;

	private String status;
	
	private PtApplication ptApplication;
	
	private String grbgApplicationNumber;
	
	private PtOldDetails grbgOldDetails;

	private AuditDetails auditDetails;

	private List<PtCollectionUnit> grbgCollectionUnits;

	private List<PtAddress> addresses;

    private JsonNode additionalDetail = null;

	private List<PropertyAccount> childGarbageAccounts;
	
	private String parentAccount;
	
	private Boolean isActive = false;
	
	private Long subAccountCount;
	
	private Long approvalDate;
	
	private String businessService;
	
	private String channel;

	
}