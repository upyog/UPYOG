package org.egov.ptr.service;

import static org.egov.ptr.util.PTRConstants.ACTION_ABOUT_TO_EXPIRE;
import static org.egov.ptr.util.PTRConstants.ACTION_EXPIRE;
import static org.egov.ptr.util.PTRConstants.STATUS_EXPIRED;
import static org.egov.ptr.util.PTRConstants.STATUS_REGISTRATIONCOMPLETED;

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
	 * Application status and workflow update for pet applications whose validity has expired.
	 */
	public void processExpiredPetApplications() {
		log.info("Starting process for updating expired pet applications...");

		long validityDateUnix = commonUtils.getTodaysEpoch(); // Get today's date since it will run after end of financial year date
		List<String> tenantIds = repository.fetchPetApplicationTenantIds(); // Get all distinct tenant IDs

		if (CollectionUtils.isEmpty(tenantIds)) {
			log.info("No tenant IDs found for processing.");
			return;
		}

		RequestInfo requestInfo = getRequestInfoWithUser(); // Get system user for updating workflow and notification
		tenantIds.forEach(tenantId -> updateStatusForExpiredApplications(tenantId, validityDateUnix, requestInfo));

		log.info("Process for updating expired pet applications completed.");
	}

	/**
	 * Sends advance notifications to pet owners for applications nearing expiration.
	 */
	public void notifyPetOwnersOfExpiringApplications() {
		log.info("Starting notification process for applications nearing expiration...");

		long validityDateUnix = commonUtils.calculateNextMarch31InEpoch(); // Calculate the next financial year-end date since it is triggered before validity date
		List<String> tenantIds = repository.fetchPetApplicationTenantIds(); // Get all distinct tenant IDs

		if (CollectionUtils.isEmpty(tenantIds)) {
			log.info("No tenant IDs found for processing.");
			return;
		}

		RequestInfo requestInfo = getRequestInfoWithUser(); // Fetch system user for notifications
		tenantIds.forEach(tenantId -> processAdvanceNotification(tenantId, validityDateUnix, requestInfo));

		log.info("Notification process for applications nearing expiration completed.");
	}

	/**
	 * Fetches the system user to create a RequestInfo object.
	 */
	private RequestInfo getRequestInfoWithUser() {
		UserDetailResponse userDetailResponse = userService.searchByUserName(config.getInternalMicroserviceUserName(),
				config.getStateLevelTenantId());

		if (userDetailResponse == null || CollectionUtils.isEmpty(userDetailResponse.getUser())) {
			log.warn("System user not found. Skipping processing.");
			return null;
		}

		return RequestInfo.builder().userInfo(userDetailResponse.getUser().get(0)).build();
	}

	/**
	 * Updates the status of expired pet applications for a specific tenant.
	 */
	private void updateStatusForExpiredApplications(String tenantId, long validityDateUnix, RequestInfo requestInfo) {
		PetApplicationSearchCriteria criteria = buildSearchCriteria(tenantId, validityDateUnix,
				STATUS_REGISTRATIONCOMPLETED);// get applications which registration has been completed and validity data is less than today's date

		List<PetRegistrationApplication> applications = repository.getApplications(criteria); // Fetch applications
		if (CollectionUtils.isEmpty(applications)) {
			log.info("No applications found for tenant: {}", tenantId);
			return;
		}

		updateApplicationStatusAndNotify(requestInfo, applications); // Process expiration and notifications
	}

	/**
	 * Processes notifications for applications nearing expiration
	 */
	private void processAdvanceNotification(String tenantId, long validityDateUnix, RequestInfo requestInfo) {
		List<PetRegistrationApplication> applications = getApplicationsForNotification(tenantId, validityDateUnix);
		if (CollectionUtils.isEmpty(applications)) {
			log.info("No applications found for tenant: {}", tenantId);
			return;
		}

		sendAdvanceNotificationsToOwners(requestInfo, applications);
	}

	/**
	 * Fetches applications nearing expiration for notification purposes.
	 */
	private List<PetRegistrationApplication> getApplicationsForNotification(String tenantId, long validityDateUnix) {
		PetApplicationSearchCriteria criteria = buildSearchCriteria(tenantId, validityDateUnix,
				STATUS_REGISTRATIONCOMPLETED);// get applications which registration has been completed and validity data is financial year end date
		return repository.getApplications(criteria);
	}

	/**
	 * Sends advance notifications to pet owners.
	 */
	private void sendAdvanceNotificationsToOwners(RequestInfo requestInfo, List<PetRegistrationApplication> applications) {
		applications.forEach(app -> app.getWorkflow().setAction(ACTION_ABOUT_TO_EXPIRE));// sending this action to send about to expire notification

		PetRegistrationRequest petRequest = new PetRegistrationRequest(requestInfo, applications);
		notificationService.process(petRequest); // call notification service
	}

	/**
	 * Updates the status of expired applications and triggers necessary
	 * notifications.
	 */
	private void updateApplicationStatusAndNotify(RequestInfo requestInfo,
			List<PetRegistrationApplication> applications) {
		applications.forEach(app -> {
			app.setExpireFlag(true);
			app.setStatus(STATUS_EXPIRED);// set application status to Expired
			app.getWorkflow().setAction(ACTION_EXPIRE);// This is added to get expired application notification in notification service
		});

		PetRegistrationRequest petRequest = new PetRegistrationRequest(requestInfo, applications);

		updateApplicationStatus(petRequest); // Push updated status to Kafka
		updateWorkflowStatus(petRequest); // Update workflow status in the system
		notificationService.process(petRequest); // Send notifications
	}

	/**
	 * Builds the search criteria for fetching applications based on tenant,
	 * validity date, and status.
	 */
	private PetApplicationSearchCriteria buildSearchCriteria(String tenantId, long validityDateUnix, String status) {
		return PetApplicationSearchCriteria.builder().validityDate(validityDateUnix).status(status).tenantId(tenantId)
				.build();
	}

	/**
	 * Updates the application status by pushing to update topic
	 */
	private void updateApplicationStatus(PetRegistrationRequest petRequest) {
		try {
			producer.push(config.getUpdatePtrTopic(), petRequest);
		} catch (Exception e) {
			log.error("Failed to push pet applications to Kafka.", e);
		}
	}

	/**
	 * Update the workflow of applications.
	 */
	private void updateWorkflowStatus(PetRegistrationRequest petRequest) {
		try {
			workflowIntegrator.updateWorkflowStatus(petRequest);
		} catch (Exception e) {
			log.error("Workflow status update failed.", e);
		}
	}
}
