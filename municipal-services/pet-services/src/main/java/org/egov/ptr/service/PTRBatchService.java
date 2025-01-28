package org.egov.ptr.service;

import static org.egov.ptr.util.PTRConstants.ACTION_ABOUT_TO_EXPIRE;
import static org.egov.ptr.util.PTRConstants.ACTION_EXPIRE;
import static org.egov.ptr.util.PTRConstants.STATUS_EXPIRED;
import static org.egov.ptr.util.PTRConstants.STATUS_REGISTRATIONCOMPLETED;
import static org.egov.ptr.util.PTRConstants.SYSTEM_CITIZEN_TENANTID;
import static org.egov.ptr.util.PTRConstants.SYSTEM_CITIZEN_USERNAME;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.models.user.UserDetailResponse;
import org.egov.ptr.producer.Producer;
import org.egov.ptr.repository.PetRegistrationRepository;
import org.egov.ptr.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PTRBatchService {

	@Autowired
	private PetConfiguration config;

	@Autowired
	private PetRegistrationRepository repository;

	@Autowired
	private WorkflowService workflowIntegrator;

	@Autowired
	private Producer producer;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonUtils commonUtils;

	@Autowired
	private PTRNotificationService notificationService;

	/**
	 * Searches pet applications, expires their status according to their validity
	 * date.
	 */
	/**
	 * Handles expiration of pet applications based on their validity date.
	 */
	public void processPetApplicationExpiration() {
		log.info("Starting pet application expiration process...");

		long validityDateUnix = commonUtils.getTodaysEpoch();
		List<String> tenantIds = repository.fetchPetApplicationTenantIds();

		if (CollectionUtils.isEmpty(tenantIds)) {
			log.info("No tenant IDs found for processing.");
			return;
		}

		RequestInfo requestInfo = getRequestInfoWithUser();
		tenantIds.forEach(tenantId -> handleExpirationForTenant(tenantId, validityDateUnix, requestInfo));

		log.info("Pet application expiration process completed.");
	}

	/**
	 * Sends advance notifications to pet owners about applications nearing
	 * expiration.
	 */
	public void sendAdvanceNotificationsToPetOwners() {
		log.info("Starting advance notification process for pet owners...");

		long validityDateUnix = commonUtils.calculateNextMarch31InEpoch();
		List<String> tenantIds = repository.fetchPetApplicationTenantIds();

		if (CollectionUtils.isEmpty(tenantIds)) {
			log.info("No tenant IDs found for processing.");
			return;
		}

		RequestInfo requestInfo = getRequestInfoWithUser();
		tenantIds.forEach(tenantId -> fetchAndNotifyApplications(tenantId, validityDateUnix, requestInfo));

		log.info("Advance notification process completed.");
	}

	private RequestInfo getRequestInfoWithUser() {
		UserDetailResponse userDetailResponse = userService.searchByUserName(SYSTEM_CITIZEN_USERNAME,
				SYSTEM_CITIZEN_TENANTID);

		if (userDetailResponse == null || CollectionUtils.isEmpty(userDetailResponse.getUser())) {
			log.warn("User not found. Skipping processing.");
			return null;
		}

		return RequestInfo.builder().userInfo(userDetailResponse.getUser().get(0)).build();
	}

	private void handleExpirationForTenant(String tenantId, long validityDateUnix, RequestInfo requestInfo) {
		PetApplicationSearchCriteria criteria = buildSearchCriteria(tenantId, validityDateUnix,
				STATUS_REGISTRATIONCOMPLETED);

		int offset = 0;
		while (true) {
			log.info("Fetching applications for tenant: {} with offset: {}", tenantId, offset);

			List<PetRegistrationApplication> applications = repository.getApplications(criteria);
			if (CollectionUtils.isEmpty(applications))
				break;

			processApplicationExpiration(requestInfo, applications);

			offset += applications.size();
		}
	}

	private void fetchAndNotifyApplications(String tenantId, long validityDateUnix, RequestInfo requestInfo) {
		PetApplicationSearchCriteria criteria = buildSearchCriteria(tenantId, validityDateUnix,
				STATUS_REGISTRATIONCOMPLETED);

		List<PetRegistrationApplication> applications = repository.getApplications(criteria);
		if (CollectionUtils.isEmpty(applications)) {
			log.info("No applications found for tenant: {}", tenantId);
			return;
		}

		applications.forEach(app -> app.getWorkflow().setAction(ACTION_ABOUT_TO_EXPIRE));

		PetRegistrationRequest petRequest = new PetRegistrationRequest(requestInfo, applications);
		notificationService.process(petRequest);
	}

	private void processApplicationExpiration(RequestInfo requestInfo, List<PetRegistrationApplication> applications) {
		applications.forEach(app -> {
			app.setExpireFlag(true);
			app.setStatus(STATUS_EXPIRED);
			app.getWorkflow().setAction(ACTION_EXPIRE);
		});

		PetRegistrationRequest petRequest = new PetRegistrationRequest(requestInfo, applications);

		updateApplicationStatus(petRequest);
		updateWorkflowStatus(petRequest);
		notificationService.process(petRequest);
	}

	private PetApplicationSearchCriteria buildSearchCriteria(String tenantId, long validityDateUnix, String status) {
		return PetApplicationSearchCriteria.builder().validityDate(validityDateUnix).status(status).tenantId(tenantId)
				.build();
	}

	private void updateApplicationStatus(PetRegistrationRequest petRequest) {
		try {
			producer.push(config.getUpdatePtrTopic(), petRequest);
		} catch (Exception e) {
			log.error("Failed to push pet applications to Kafka.", e);
		}
	}

	private void updateWorkflowStatus(PetRegistrationRequest petRequest) {
		try {
			workflowIntegrator.updateWorkflowStatus(petRequest);
		} catch (Exception e) {
			log.error("Workflow status update failed.", e);
		}
	}

}
