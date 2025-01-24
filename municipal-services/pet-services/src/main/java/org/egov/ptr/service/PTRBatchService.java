package org.egov.ptr.service;

import static org.egov.ptr.util.PTRConstants.STATUS_APPROVED;
import static org.egov.ptr.util.PTRConstants.STATUS_EXPIRED;
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
import org.egov.ptr.util.PetUtil;
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
	private PetUtil petUtil;

	/**
	 * Searches pet applications, expires their status according to their validity
	 * date.
	 */
	public void getPetApplicationsAndPerformAction() {
		log.info("Starting pet application expiration process...");

		long validityDateUnix = petUtil.getTodaysEpoch();

		List<String> tenantIds = repository.fetchPetApplicationTenantIds();
		if (CollectionUtils.isEmpty(tenantIds)) {
			log.info("No tenant IDs found for processing.");
			return;
		}

		tenantIds.forEach(tenantId -> processTenant(tenantId, validityDateUnix));

		log.info("Pet application expiration process completed.");
	}

	private void processTenant(String tenantId, long validityDateUnix) {
		try {
			RequestInfo requestInfo = getRequestInfoWithUser(tenantId);
			if (requestInfo == null)
				return;

			processTenantApplications(tenantId, validityDateUnix, requestInfo);
		} catch (Exception ex) {
			log.error("Batch process failed for tenant ID: {}", tenantId, ex);
		}
	}

	private RequestInfo getRequestInfoWithUser(String tenantId) {
		UserDetailResponse userDetailResponse = userService.searchByUserName(SYSTEM_CITIZEN_USERNAME, tenantId);
		if (userDetailResponse == null || userDetailResponse.getUser().isEmpty()) {
			log.warn("User not found for tenant '{}'. Skipping processing.", tenantId);
			return null;
		}
		return RequestInfo.builder().userInfo(userDetailResponse.getUser().get(0)).build();
	}

	private void processTenantApplications(String tenantId, long validityDateUnix, RequestInfo requestInfo) {
		PetApplicationSearchCriteria criteria = PetApplicationSearchCriteria.builder().validityDate(validityDateUnix)
				.status(STATUS_APPROVED).tenantId(tenantId).build();

		int offset = 0;
		while (true) {
			log.info("Fetching applications for tenant: {} with offset: {}", tenantId, offset);

			List<PetRegistrationApplication> petApplications = repository.getApplications(criteria);
			if (CollectionUtils.isEmpty(petApplications))
				break;

			expireAndUpdatePetApplications(requestInfo, petApplications);

			offset += petApplications.size();
		}
	}

	private void expireAndUpdatePetApplications(RequestInfo requestInfo,
			List<PetRegistrationApplication> applications) {
		applications.forEach(app -> {
			app.setExpireFlag(true);
			app.setStatus(STATUS_EXPIRED);
		});

		PetRegistrationRequest petRequest = new PetRegistrationRequest(requestInfo, applications);

		pushToKafka(petRequest);
		updateWorkflowStatus(petRequest);
	}

	private void pushToKafka(PetRegistrationRequest petRequest) {
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
