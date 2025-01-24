package org.egov.ptr.service;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
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
import org.egov.ptr.util.PetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.jaegertracing.thriftjava.Log;
import lombok.extern.slf4j.Slf4j;

import static org.egov.ptr.util.PTRConstants.*;

@Slf4j
@Service
public class EnrichmentService {

	@Autowired
	private PetConfiguration config;

	@Autowired
	private UserService userService;

	@Autowired
	private PetUtil petUtil;

	public void enrichPetApplication(PetRegistrationRequest petRegistrationRequest) {
		RequestInfo requestInfo = petRegistrationRequest.getRequestInfo();
		List<PetRegistrationApplication> applications = petRegistrationRequest.getPetRegistrationApplications();
		String tenantId = applications.get(0).getTenantId();

		// Generate a list of application numbers using ID generator
		List<String> petRegistrationIdList = petUtil.getIdList(requestInfo, tenantId, config.getPetIdGenName(),
				config.getPetIdGenFormat(), applications.size());

		// Prepare audit details once (can be reused across applications)
		AuditDetails commonAuditDetails = AuditDetails.builder().createdBy(requestInfo.getUserInfo().getUuid())
				.createdTime(System.currentTimeMillis()).lastModifiedBy(requestInfo.getUserInfo().getUuid())
				.lastModifiedTime(System.currentTimeMillis()).build();

		long validityDateUnix = petUtil.calculateNextMarch31At8PMInEpoch();

		int index = 0;
		for (PetRegistrationApplication application : applications) {

			// Set common audit details, ID, and application number
			application.setAuditDetails(commonAuditDetails);
			application.setId(UUID.randomUUID().toString());
			application.setApplicationNumber(petRegistrationIdList.get(index++));
			application.setValidityDate(validityDateUnix);
			application.setStatus(STATUS_APPLIED);
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

	private boolean isNewPetApplication(PetRegistrationApplication application) {
		return NEW_PET_APPLICATION.equals(application.getApplicationType())
				&& (application.getPetToken().isEmpty() || application.getPetToken() == null);
	}

	private boolean isRenewPetApplication(PetRegistrationApplication application) {
		return RENEW_PET_APPLICATION.equals(application.getApplicationType());
	}

	private void enrichNewPetToken(PetRegistrationApplication application, RequestInfo requestInfo, String tenantId) {
		String petTokenId = petUtil
				.getIdList(requestInfo, tenantId, config.getPetTokenName(), config.getPetTokenFormat(), 1).get(0);
		application.setPetToken(petTokenId);
	}

	private void enrichAddress(PetRegistrationApplication application) {
		Address address = application.getAddress();
		address.setRegistrationId(application.getId());
		address.setId(UUID.randomUUID().toString());
	}

	private void enrichPetDetails(PetRegistrationApplication application) {
		PetDetails petDetails = application.getPetDetails();
		petDetails.setPetDetailsId(application.getId());
		petDetails.setId(UUID.randomUUID().toString());
	}

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



	public void enrichPetApplicationUponUpdate(PetRegistrationRequest petRegistrationRequest) {
		// Enrich lastModifiedTime and lastModifiedBy in case of update
		for (PetRegistrationApplication application : petRegistrationRequest.getPetRegistrationApplications()) {
			application.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
			application.getAuditDetails()
					.setLastModifiedBy(petRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
			if (application.getWorkflow().getAction().equals(ACTION_VERIFY)) {
				application.setStatus(STATUS_DOCVERIFIED);
			} else if (application.getWorkflow().getAction().equals(ACTION_REJECT)) {
				application.setStatus(STATUS_REJECTED);
			} else if (application.getWorkflow().getAction().equals(ACTION_APPROVE)) {
				application.setStatus(STATUS_APPROVED);
				if (isNewPetApplication(application)) {
					enrichNewPetToken(application, petRegistrationRequest.getRequestInfo(), application.getTenantId());
					log.info("Pet Token Generated : " + application.getPetToken());
				}
			}
		}
	}
}
