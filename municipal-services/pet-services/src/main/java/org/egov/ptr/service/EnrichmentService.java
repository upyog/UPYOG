package org.egov.ptr.service;

import java.util.List;
import java.util.UUID;

import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.AuditDetails;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.util.PetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import ch.qos.logback.classic.Logger;

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

	public void enrichPetApplicationUponUpdate(PetRegistrationRequest petRegistrationRequest) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		petRegistrationRequest.getPetRegistrationApplications().get(0).getAuditDetails()
				.setLastModifiedTime(System.currentTimeMillis());
		petRegistrationRequest.getPetRegistrationApplications().get(0).getAuditDetails()
				.setLastModifiedBy(petRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
	}

}
