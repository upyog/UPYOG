package org.upyog.sv.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.repository.StreetVendingRepository;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;

import digit.models.coremodels.UserDetailResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulerService {
	@Autowired
	private StreetVendingRepository streetVendingRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private StreetyVendingNotificationService notificationService;

	private static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L;
	private static final long TWO_MONTHS_MILLIS = 60L * ONE_DAY_MILLIS; // Approx. 2 months

	@Value
	("${scheduler.sv.expiry.enabled:false}") 
	private boolean isSchedulerEnabled;
	// Scheduler runs daily at 1 AM
	@Scheduled(cron = "0 0 1 * * *")
	public void handleStreetVendingApplications() {
		if (!isSchedulerEnabled) {
	        log.info("Scheduler is disabled via configuration.");
	        return;
	    }
		log.info("Street Vending Applications Scheduler started");
		processExpiredApplications();
		processApplicationsNearExpiry();
	}

	/**
	 * Processes applications that have expired (validity date reached)
	 */
	private void processExpiredApplications() {
		List<StreetVendingDetail> expiredApplications = fetchApplicationsByValidity(System.currentTimeMillis());
		updateAndNotifyApplications(expiredApplications, StreetVendingConstants.ACTION_STATUS_APPLICATION_EXPIRED,
				true);
	}

	/**
	 * Processes applications that are about to expire in 2 months
	 */
	private void processApplicationsNearExpiry() {
		long nearExpiryDate = System.currentTimeMillis() + TWO_MONTHS_MILLIS; // Two months before expiry
		List<StreetVendingDetail> nearExpiryApplications = fetchApplicationsByValidity(nearExpiryDate);
		updateAndNotifyApplications(nearExpiryApplications, StreetVendingConstants.ACTION_STATUS_ELIGIBLE_TO_RENEW,
				false);
	}

	/**
	 * Fetch applications by validity date and status
	 */
	private List<StreetVendingDetail> fetchApplicationsByValidity(Long validityDate) {
		StreetVendingSearchCriteria searchCriteria = StreetVendingSearchCriteria.builder().validityDate(validityDate)
				.status(StreetVendingConstants.REGISTRATION_COMPLETED).build();
		return streetVendingRepository.getStreetVendingApplications(searchCriteria);
	}

	/**
	 * Updates application details and sends notifications
	 */
	private void updateAndNotifyApplications(List<StreetVendingDetail> applications, String action,
			boolean markExpired) {
		if (applications.isEmpty())
			return;

		UserDetailResponse userDetailResponse = userService.searchByUserName("9999009999", "pg.citya");

		for (StreetVendingDetail detail : applications) {
			detail.setEligibleToRenew(true);
			detail.getWorkflow().setAction(action);
			if (markExpired) {
				detail.setExpireFlag(true);
			}

			StreetVendingRequest streetVendingRequest = StreetVendingRequest.builder()
					.requestInfo(RequestInfo.builder().userInfo(userDetailResponse.getUser().get(0)).build())
					.streetVendingDetail(detail).build();

			streetVendingRepository.save(streetVendingRequest);
			notificationService.process(streetVendingRequest);
		}
	}
}
