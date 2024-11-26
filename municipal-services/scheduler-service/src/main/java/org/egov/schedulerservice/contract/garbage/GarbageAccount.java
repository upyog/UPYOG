package org.egov.schedulerservice.contract.garbage;

import java.util.List;

import org.egov.schedulerservice.dto.AuditDetails;

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
public class GarbageAccount {

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

	private String declarationUuid;

	private String workflowAction;

	private String workflowComment;
	
	private Boolean isOnlyWorkflowCall = false;

	private String status;
	
	private GrbgApplication grbgApplication;
	
	private String grbgApplicationNumber;
	
	private GrbgOldDetails grbgOldDetails;

	private GrbgCommercialDetails grbgCommercialDetails;
	
	private List<GrbgDocument> documents;

	private AuditDetails auditDetails;
	
//	private List<GarbageBill> garbageBills;

	private List<GrbgCollectionUnit> grbgCollectionUnits;

	private List<GrbgAddress> addresses;

    private JsonNode additionalDetail = null;

	private List<GarbageAccount> childGarbageAccounts;
	
	private String parentAccount;
	
	private Boolean isActive = false;
	
	private Long subAccountCount;
	
}