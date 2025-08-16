package org.egov.ptr.service;

import static org.egov.ptr.util.PTRConstants.ACTION_APPROVE;
import static org.egov.ptr.util.PTRConstants.ACTION_REJECT;
import static org.egov.ptr.util.PTRConstants.ACTION_VERIFY;
import static org.egov.ptr.util.PTRConstants.NEW_PET_APPLICATION;
import static org.egov.ptr.util.PTRConstants.RENEW_PET_APPLICATION;
import static org.egov.ptr.util.PTRConstants.STATUS_APPLIED;
import static org.egov.ptr.util.PTRConstants.STATUS_APPROVED;
import static org.egov.ptr.util.PTRConstants.STATUS_DOCVERIFIED;
import static org.egov.ptr.util.PTRConstants.STATUS_REJECTED;

import java.util.List;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.Address;
import org.egov.ptr.models.AuditDetails;
import org.egov.ptr.models.PetDetails;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.models.PetRenewalAuditDetails;
import org.egov.ptr.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;



/**
 * Service class responsible for enriching pet registration applications with necessary data
 * such as application number, audit details, address, and pet details before processing.
 */
@Slf4j
@Service
public class EnrichmentService {

	@Autowired
	private PetConfiguration config;

	@Autowired
	private CommonUtils commonUtils;

	/**
	 * Enriches the pet registration application by assigning unique IDs, application numbers,
	 * and setting audit details.
	 *
	 * @param petRegistrationRequest The request containing pet registration applications.
	 */
	public void enrichPetApplication(PetRegistrationRequest petRegistrationRequest) {
		RequestInfo requestInfo = petRegistrationRequest.getRequestInfo();
		List<PetRegistrationApplication> applications = petRegistrationRequest.getPetRegistrationApplications();
		String tenantId = applications.get(0).getTenantId();

		// Generate a list of application numbers using ID generator
		List<String> petRegistrationIdList = commonUtils.getIdList(requestInfo, tenantId, config.getPetIdGenName(),
				config.getPetIdGenFormat(), applications.size());

		// Prepare audit details once (can be reused across applications)
		AuditDetails commonAuditDetails = AuditDetails.builder().createdBy(requestInfo.getUserInfo().getUuid())
				.createdTime(System.currentTimeMillis()).lastModifiedBy(requestInfo.getUserInfo().getUuid())
				.lastModifiedTime(System.currentTimeMillis()).build();

		long validityDateUnix = commonUtils.calculateNextMarch31InEpoch();

		int index = 0;
		for (PetRegistrationApplication application : applications) {

			// Set common audit details, ID, and application number
			application.setAuditDetails(commonAuditDetails);
			application.setId(UUID.randomUUID().toString());
			application.setApplicationNumber(petRegistrationIdList.get(index++));
			application.setValidityDate(validityDateUnix);
			application.setExpireFlag(false);

			// Enrich address and pet details
			enrichAddress(application);
			enrichPetDetails(application);

			if (isRenewPetApplication(application)) {
				enrichRenewalDetails(application, validityDateUnix);
			}

			// Enrich documents if any
			if (!CollectionUtils.isEmpty(application.getDocuments())) {
				enrichDocuments(application);
			}
		}
	}

	/**
	 * Checks if the application is a new pet registration.
	 */
	private boolean isNewPetApplication(PetRegistrationApplication application) {
		return NEW_PET_APPLICATION.equals(application.getApplicationType())
				&& (application.getPetToken().isEmpty() || application.getPetToken() == null);
	}

	/**
	 * Checks if the application is a renewal request.
	 */
	private boolean isRenewPetApplication(PetRegistrationApplication application) {
		return RENEW_PET_APPLICATION.equals(application.getApplicationType());
	}

	/**
	 * Generates a new pet token for a new pet registration application.
	 */
	private void enrichNewPetToken(PetRegistrationApplication application, RequestInfo requestInfo, String tenantId) {
		String petTokenId = commonUtils
				.getIdList(requestInfo, tenantId, config.getPetTokenName(), config.getPetTokenFormat(), 1).get(0);
		application.setPetToken(petTokenId);
	}

	/**
	 * Enriches the address details in the application.
	 */
	private void enrichAddress(PetRegistrationApplication application) {
		Address address = application.getAddress();
		address.setRegistrationId(application.getId());
		address.setId(UUID.randomUUID().toString());
	}
	/**
	 * Enriches pet details within the application.
	 */
	private void enrichPetDetails(PetRegistrationApplication application) {
		PetDetails petDetails = application.getPetDetails();
		petDetails.setPetDetailsId(application.getId());
		petDetails.setId(UUID.randomUUID().toString());
	}

	/**
	 * Enriches document details if documents are attached to the application.
	 */
	private void enrichRenewalDetails(PetRegistrationApplication application, long validityDateUnix) {
		PetRenewalAuditDetails petRenewalAuditDetails = new PetRenewalAuditDetails();
		petRenewalAuditDetails.setId(application.getPetToken());
		petRenewalAuditDetails.setApplicationNumber(application.getApplicationNumber());
		petRenewalAuditDetails.setPreviousapplicationnumber(application.getPreviousApplicationNumber());
		petRenewalAuditDetails.setExpiryDate(validityDateUnix);
		petRenewalAuditDetails.setRenewalDate(System.currentTimeMillis());
		petRenewalAuditDetails.setTokenNumber(application.getPetToken());
		petRenewalAuditDetails.setPetRegistrationId(application.getId());

	}

	/**
	 * Updates application status and enriches last modified details upon update.
	 */
	private void enrichDocuments(PetRegistrationApplication application) {
		application.getDocuments().forEach(doc -> {
			if (doc.getId() == null) {
				doc.setId(UUID.randomUUID().toString());
				doc.setActive(true);
				doc.setTenantId(application.getTenantId());
				doc.setAuditDetails(application.getAuditDetails());
			}
		});
	}

	public void enrichPetApplicationUponUpdate(String status, PetRegistrationRequest petRegistrationRequest) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		PetRegistrationApplication application = petRegistrationRequest.getPetRegistrationApplications().get(0);
		application.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
		application.getAuditDetails()
				.setLastModifiedBy(petRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		application.setStatus(status);
		if (application.getWorkflow().getAction().equals(ACTION_APPROVE)) {

			if (isNewPetApplication(application)) {
				enrichNewPetToken(application, petRegistrationRequest.getRequestInfo(), application.getTenantId());
				log.info("Pet Token Generated : " + application.getPetToken());
			}
		}
	}
}
