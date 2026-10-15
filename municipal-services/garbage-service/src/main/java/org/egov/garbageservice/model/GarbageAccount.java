package org.egov.garbageservice.model;

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
public class GarbageAccount {

	private Long id;
	
	private String uuid;

	private String tenantId;

	private Long garbageId;

	private String propertyId;
	
	private String systemPropertyId;
	
	private String himParivarId;
	
	private Boolean isDdpVerified;

	private Boolean ddpPrintVerified;

	private Long ddpModifiedDate;

	/**
	 * The DDP workflow fields below (isReadyForPrinting .. remarks) are stored
	 * in eg_grbg_account_ddp, not eg_grbg_account, and are populated on search
	 * via a left join on the account uuid.
	 *
	 * Set true in bulk by the DDP printing scheduler once ddpVerified accounts
	 * in a ULB/ward (as configured in the ULBS.DdpPrinting MDMS master) are
	 * ready to be handed off for door plate printing.
	 */
	private Boolean isReadyForPrinting;

	/**
	 * Vendor's print-verification outcome, e.g. "VERIFIED"/"REJECTED"; null
	 * while pending.
	 */
	private String vendorPrintVerified;

	private Boolean ulbVerified;

	private Boolean installationDone;

	private String ddpLatitude;

	private String ddpLongitude;

	/**
	 * Set by the printing vendor once the physical plate has been printed.
	 */
	private Boolean ddpPrintingDone;

	/**
	 * Set by the printing vendor once the printed plate has been dispatched
	 * to the ULB/installer.
	 */
	private Boolean ddpDispatched;

	private String ddpRejectionReason;

	private String remarks;

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
	
	@Builder.Default
	private Boolean isDdpUpdateCall = false;

	private String status;
	
	private GrbgApplication grbgApplication;
	
	private String grbgApplicationNumber;
	
	private GrbgOldDetails grbgOldDetails;

//	private GrbgCommercialDetails grbgCommercialDetails;
	
//	private List<GrbgDocument> documents;

	private AuditDetails auditDetails;
	
//	private List<GarbageBill> garbageBills;

	private List<GrbgCollectionUnit> grbgCollectionUnits;

	private List<GrbgAddress> addresses;

    private JsonNode additionalDetail = null;

	private List<GarbageAccount> childGarbageAccounts;
	
	private String parentAccount;
	
	private Boolean isActive = false;
	
	private Long subAccountCount;
	
	private Long approvalDate;
	
	private String businessService;
	
	private String channel;

	
}