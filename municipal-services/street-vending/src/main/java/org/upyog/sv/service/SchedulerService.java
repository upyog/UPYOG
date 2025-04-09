package org.upyog.sv.service;

import java.time.LocalDate;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.repository.StreetVendingRepository;
import org.upyog.sv.util.StreetVendingUtil;
import org.upyog.sv.web.models.*;

import digit.models.coremodels.UserDetailResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SchedulerService {
	@Autowired
	private StreetVendingRepository streetVendingRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private StreetyVendingNotificationService notificationService;

	@Value("${scheduler.sv.expiry.enabled:false}")
	private boolean isSchedulerEnabled;

	/**
	 * Scheduled job to handle street vending applications. Runs daily at 1 AM if
	 * enabled via configuration.
	 */
	@Scheduled(cron = "0 0 1 * * *")
	public void processStreetVendingApplications() {
		if (!isSchedulerEnabled) {
			log.info("Scheduler is disabled via configuration.");
			return;
		}
		log.info("Street Vending Applications Scheduler started");

		markEligibleForRenewalAndNotify();
		markExpiredApplicationsAndNotify();
	}

	/**
	 * Marks applications as expired if their validity date has passed. Sends
	 * notifications to applicants.
	 */
	private void markExpiredApplicationsAndNotify() {
		List<StreetVendingDetail> expiredApplications = fetchApplicationsByValidity(StreetVendingUtil.getCurrentDate());
		if (expiredApplications.isEmpty()) {
			log.info("No expired applications found");
			return;
		}
		updateApplicationStatusAndNotify(expiredApplications, StreetVendingConstants.ACTION_STATUS_APPLICATION_EXPIRED,
				true);
	}

	/**
	 * Marks applications as eligible for renewal if they are expiring in 2 months.
	 * Sends renewal reminder notifications.
	 */
	private void markEligibleForRenewalAndNotify() {
		LocalDate expiryThresholdDate = StreetVendingUtil.getCurrentDateFromMonths(2); // Applications expiring in 2
																						// months
		List<StreetVendingDetail> nearExpiryApplications = fetchApplicationsByValidity(expiryThresholdDate);
		if (nearExpiryApplications.isEmpty()) {
			log.info("No applications found for renewal eligibility");
			return;
		}
		updateApplicationStatusAndNotify(nearExpiryApplications, StreetVendingConstants.ACTION_STATUS_ELIGIBLE_TO_RENEW,
				false);
	}

	/**
	 * Fetches applications that match the given validity date and have completed
	 * registration.
	 * 
	 * @param validityDate The date to filter applications by.
	 * @return List of StreetVendingDetail that match the criteria.
	 */
	private List<StreetVendingDetail> fetchApplicationsByValidity(LocalDate validityDate) {
		StreetVendingSearchCriteria searchCriteria = StreetVendingSearchCriteria.builder().validityDate(validityDate)
				.status(StreetVendingConstants.REGISTRATION_COMPLETED).build();

		return streetVendingRepository.getStreetVendingApplications(searchCriteria);
	}

	/**
	 * Updates application statuses based on expiration or renewal eligibility and
	 * sends notifications.
	 * 
	 * @param applications List of applications to update.
	 * @param action       The action to set in the workflow.
	 * @param markExpired  If true, marks the application as expired.
	 */
	private void updateApplicationStatusAndNotify(List<StreetVendingDetail> applications, String action,
			boolean markExpired) {
		if (applications.isEmpty()) {
			log.info("No applications found for action: {}", action);
			return;
		}

		UserDetailResponse systemUser = userService.searchByUserName(StreetVendingConstants.SYSTEM_CITIZEN_USERNAME,
				StreetVendingConstants.SYSTEM_CITIZEN_TENANTID);
		if (systemUser == null || systemUser.getUser() == null || systemUser.getUser().isEmpty()) {
			log.error("System user not found");
			return;
		}
		// create workflow object for the action for notification
		Workflow workflow = Workflow.builder().action(action).build();
		// Iterate through each application and update the renewal status or expire flag and application status if markExpired flag is true
		for (StreetVendingDetail detail : applications) {
			detail.setRenewalStatus(RenewalStatus.ELIGIBLE_TO_RENEW);
			detail.setWorkflow(workflow);

			if (markExpired) {
				detail.setExpireFlag(true);
				detail.setApplicationStatus(StreetVendingConstants.STATUS_EXPIRED);
			}
			StreetVendingRequest streetVendingRequest = StreetVendingRequest.builder()
					.requestInfo(RequestInfo.builder().userInfo(systemUser.getUser().get(0)).build())
					.streetVendingDetail(detail).build();

			streetVendingRepository.update(streetVendingRequest);
			notificationService.process(streetVendingRequest, action);
		}
	}
}
