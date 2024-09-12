package org.egov.ptr.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.BooleanUtils;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.AuditDetails;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.util.PTRConstants;
import org.egov.ptr.util.PetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class EnrichmentService {

	@Autowired
	private PetConfiguration config;

	@Autowired
	private UserService userService;

	@Autowired
	private PetUtil petUtil;

	public void enrichPetApplication(PetRegistrationRequest petRegistrationRequest) {
		// List<String> petRegistrationIdList =
		// idgenUtil.getIdList(petRegistrationRequest.getRequestInfo(),
		// petRegistrationRequest.getPetRegistrationApplications().get(0).getTenantId(),
		// "ptr.registrationid", "",
		// petRegistrationRequest.getPetRegistrationApplications().size());
		List<String> petRegistrationIdList = petUtil.getIdList(petRegistrationRequest.getRequestInfo(),
				petRegistrationRequest.getPetRegistrationApplications().get(0).getTenantId(), config.getPetIdGenName(),
				config.getPetIdGenFormat(), petRegistrationRequest.getPetRegistrationApplications().size());
		Integer index = 0;
		for (PetRegistrationApplication application : petRegistrationRequest.getPetRegistrationApplications()) {

			AuditDetails auditDetails = AuditDetails.builder()
					.createdBy(petRegistrationRequest.getRequestInfo().getUserInfo().getUuid())
					.createdTime(System.currentTimeMillis())
					.lastModifiedBy(petRegistrationRequest.getRequestInfo().getUserInfo().getUuid())
					.lastModifiedTime(System.currentTimeMillis()).build();
			application.setAuditDetails(auditDetails); // Enrich audit details
			application.setId(UUID.randomUUID().toString()); // Enrich UUID
			application.setStatus(PTRConstants.APPLICATION_STATUS_INITIATED);
			application.getAddress().setRegistrationId(application.getId()); // Enrich registration Id
			application.getAddress().setId(UUID.randomUUID().toString()); // Enrich address UUID
			application.getPetDetails().setPetDetailsId(application.getId());
			application.getPetDetails().setId(UUID.randomUUID().toString()); // Enrich petDetails UUID
			application.setApplicationNumber(petRegistrationIdList.get(index++)); // Enrich application number from
																					// IDgen
			if (!CollectionUtils.isEmpty(application.getDocuments()))
				application.getDocuments().forEach(doc -> {
					if (doc.getId() == null) {
						doc.setId(UUID.randomUUID().toString());
						doc.setActive(true);
						doc.setTenantId(application.getTenantId());
						doc.setAuditDetails(application.getAuditDetails());

					}
				});

			// application.setApplicationNumber(UUID.randomUUID().toString());
		}
	}

	public void enrichPetApplicationUponUpdate(PetRegistrationRequest petRegistrationRequest, PetRegistrationApplication existingApplication) {
		
		existingApplication.setWorkflow(petRegistrationRequest.getPetRegistrationApplications().get(0).getWorkflow());
		existingApplication.setIsOnlyWorkflowCall(petRegistrationRequest.getPetRegistrationApplications().get(0).getIsOnlyWorkflowCall());
		
		if(BooleanUtils.isTrue(petRegistrationRequest.getPetRegistrationApplications().get(0).getIsOnlyWorkflowCall())
				&& null != petRegistrationRequest.getPetRegistrationApplications().get(0).getWorkflow()) {
			String status = getStatusOrAction(petRegistrationRequest.getPetRegistrationApplications().get(0).getWorkflow().getAction(), false);
			petRegistrationRequest.setPetRegistrationApplications(Collections.singletonList(existingApplication));
			petRegistrationRequest.getPetRegistrationApplications().get(0).setStatus(status);
		}
		
		// Enrich in case of update
		petRegistrationRequest.getPetRegistrationApplications().get(0).setStatus(getStatusOrAction(petRegistrationRequest.getPetRegistrationApplications().get(0).getWorkflow().getAction(),true));
		petRegistrationRequest.getPetRegistrationApplications().get(0).setAuditDetails(existingApplication.getAuditDetails());
		petRegistrationRequest.getPetRegistrationApplications().get(0).getAuditDetails()
				.setLastModifiedTime(System.currentTimeMillis());
		petRegistrationRequest.getPetRegistrationApplications().get(0).getAuditDetails()
				.setLastModifiedBy(petRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		
		// enrich documents
		if(null != petRegistrationRequest.getPetRegistrationApplications()
				&& !CollectionUtils.isEmpty(petRegistrationRequest.getPetRegistrationApplications().get(0).getDocuments())) {
			petRegistrationRequest.getPetRegistrationApplications().get(0).getDocuments().stream().forEach(document -> {
				Optional.ofNullable(document.getAuditDetails()).ifPresent(auditDetails -> {
				    Optional.ofNullable(petRegistrationRequest.getRequestInfo())
				            .map(requestInfo -> requestInfo.getUserInfo())
				            .map(userInfo -> userInfo.getUuid())
				            .ifPresent(auditDetails::setLastModifiedBy);
				    
				    auditDetails.setLastModifiedTime(System.currentTimeMillis());
				});
			});
		}
	}


	public String getStatusOrAction(String action, Boolean fetchValue) {
		
		Map<String, String> map = new HashMap<>();
		
		map.put(PTRConstants.WORKFLOW_ACTION_INITIATE, PTRConstants.APPLICATION_STATUS_INITIATED);
        map.put(PTRConstants.WORKFLOW_ACTION_FORWARD_TO_VERIFIER, PTRConstants.APPLICATION_STATUS_PENDINGFORVERIFICATION);
        map.put(PTRConstants.WORKFLOW_ACTION_VERIFY, PTRConstants.APPLICATION_STATUS_PENDINGFORAPPROVAL);
        map.put(PTRConstants.WORKFLOW_ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT, PTRConstants.APPLICATION_STATUS_PENDINGFORPAYMENT);
        map.put(PTRConstants.WORKFLOW_ACTION_RETURN_TO_INITIATOR, PTRConstants.APPLICATION_STATUS_PENDINGFORMODIFICATION);
        map.put(PTRConstants.WORKFLOW_ACTION_FORWARD_TO_APPROVER, PTRConstants.APPLICATION_STATUS_PENDINGFORAPPROVAL);
        map.put(PTRConstants.WORKFLOW_ACTION_APPROVE, PTRConstants.APPLICATION_STATUS_APPROVED);
		
		if(!fetchValue){
			// return key
			for (Map.Entry<String, String> entry : map.entrySet()) {
		        if (entry.getValue().equals(action)) {
		            return entry.getKey();
		        }
		    }
		}
		// return value
		return map.get(action);
	}

}
