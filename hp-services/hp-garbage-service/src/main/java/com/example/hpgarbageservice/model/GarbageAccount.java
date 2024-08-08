package com.example.hpgarbageservice.model;

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
@EqualsAndHashCode(exclude = {"id","grbgApplication","grbgCommercialDetails","auditDetails","garbageBills","childGarbageAccounts"})
public class GarbageAccount {

	private Long id;
	
	private String uuid;

	private String tenantId;

	private Long garbageId;

	private Long propertyId;

	private String type;

	private String name;

	private String mobileNumber;

	private String gender;

	private String emailId;

	private Boolean isOwner; 
	
	private String userUuid;

	private String declarationUuid;

	private String workflowAction;

	private String status;
	
	private GrbgApplication grbgApplication;
	
	private GrbgOldDetails grbgOldDetails;

	private GrbgCommercialDetails grbgCommercialDetails;
	
	private List<GrbgDocument> documents;

	private AuditDetails auditDetails;
	
	private List<GarbageBill> garbageBills;

	private List<GrbgCollectionUnit> grbgCollectionUnits;

	private List<GrbgAddress> addresses;

    private JsonNode additionalDetail = null;

	private List<GarbageAccount> childGarbageAccounts;
	
}