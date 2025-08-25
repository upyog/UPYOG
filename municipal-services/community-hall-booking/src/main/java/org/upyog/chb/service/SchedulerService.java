package org.upyog.chb.service;
import static org.upyog.chb.constants.CommunityHallBookingConstants.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.BookingPaymentTimerDetails;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.workflow.ProcessInstance;
import org.upyog.chb.web.models.workflow.State;

import digit.models.coremodels.UserDetailResponse;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/**
 * This service class handles scheduled tasks for the Community Hall Booking module.
 * 
 * Purpose:
 * - To automate periodic tasks such as cleaning up expired bookings or failed payments.
 * - To ensure that the system remains consistent and free of stale or invalid data.
 * 
 * Dependencies:
 * - CommunityHallBookingRepository: Handles database operations for bookings.
 * - UserService: Fetches user details required for certain scheduled tasks.
 * - WorkflowService: Manages workflow-related operations for bookings.
 * 
 * Features:
 * - Executes scheduled tasks at fixed intervals using the @Scheduled annotation.
 * - Logs the execution of scheduled tasks for debugging and monitoring purposes.
 * - Ensures transactional consistency for database operations during scheduled tasks.
 * 
 * Methods:
 * 1. cleanupExpiredEntries:
 *    - Scheduled to run every 5 minutes.
 *    - Deletes expired bookings or bookings with failed payments from the paymentTimer table.
 * 
 * 2. deleteExpiredBookings:
 *    - Performs the actual deletion of expired or invalid bookings.
 *    - Ensures that the database remains free of stale data.
 * 
 * Usage:
 * - This class is automatically managed by Spring and executes tasks based on the configured schedule.
 * - It ensures consistent and automated cleanup operations for the module.
 */
@Slf4j
@Component
public class SchedulerService {

	@Autowired
	private CommunityHallBookingRepository bookingRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private CommunityHallBookingConfiguration config;

	/**
	 * This scheduler runs every 5 mins to delete the bookingId from the
	 * paymentTimer table when the timer is expired or payment is failed
	 * Uses ShedLock to ensure only one instance runs this job across multiple service instances
	 */
	@Scheduled(fixedRate = 5 * 60 * 1000) // Runs every 5 minutes
	@SchedulerLock(
		name = "chbCleanupExpiredEntriesJob",
		lockAtLeastFor = "PT1M",  // Hold the lock for at least 1 minute
		lockAtMostFor = "PT10M"   // Auto-release after 10 minutes if job crashes
	)
	public void cleanupExpiredEntries() {
		log.info("Delete Expired Booking task running...:::.....:::");
		deleteExpiredBookings();
	}

	@Transactional
	public void deleteExpiredBookings() {
		List<BookingPaymentTimerDetails> bookingPaymentTimerDetails = bookingRepository.getExpiredBookingTimer();
		List<String> bookingList = bookingPaymentTimerDetails.stream().map(detail -> detail.getBookingId())
				.collect(Collectors.toList());

		log.info("Expired booking id list  : {}", bookingList);

		String bookingIds = String.join(",", bookingList);

		bookingRepository.deleteBookingTimer(bookingIds, true);

	}

	/**
	 * This scheduler runs everyday midnight(1 am) to call workflow for booking
	 * applications of which booking date has crossed to initiate booking refund
	 * process
	 * Uses ShedLock to ensure only one instance runs this job across multiple service instances
	 */
	@Scheduled(cron = "0 0 1 * * *")
	@SchedulerLock(
		name = "chbUpdateWorkflowForBookedApplicationsJob",
		lockAtLeastFor = "PT1M",  // Hold the lock for at least 1 minute
		lockAtMostFor = "PT30M"   // Auto-release after 30 minutes if job crashes
	)
	public void updateWorkflowForBookedApplications() {
		log.info("Scheduler - Updating Workflow of Booked applications...");

		String formattedDate = CommunityHallBookingUtil.parseLocalDateToString(LocalDate.now().minusDays(1),
				"yyyy-MM-dd");

		List<CommunityHallBookingDetail> bookingDetails = bookingRepository.getBookingDetails(
				CommunityHallBookingSearchCriteria.builder().toDate(formattedDate).status(CHB_STATUS_BOOKED).build());

		// Exit if no booking details are found
		if (bookingDetails == null || bookingDetails.isEmpty()) {
			log.info("No booked applications found for update.");
			return;
		} else {
			String bookingNos = bookingDetails.stream().map(CommunityHallBookingDetail::getBookingNo)
					.collect(Collectors.joining(", "));
			log.info("Booking Nos: " + bookingNos);
		}
		UserDetailResponse userDetailResponse = userService.searchByUserName(config.getInternalMicroserviceUserName(), config.getStateLevelTenantId());
		if (userDetailResponse == null || userDetailResponse.getUser().isEmpty()) {
			throw new IllegalStateException("SYSTEM user not found for tenant '" + CHB_TENANTID + "'.");
		}

		RequestInfo requestInfo = RequestInfo.builder().userInfo(userDetailResponse.getUser().get(0)).build();

		ProcessInstance workflow = ProcessInstance.builder().action(CHB_ACTION_MOVETOEMPLOYEE)
				.moduleName(CHB_REFUND_WORKFLOW_MODULENAME).tenantId(CHB_TENANTID)
				.businessService(CHB_REFUND_WORKFLOW_BUSINESSSERVICE).build();

		// Process each booking detail
		bookingDetails.forEach(bookingDetail -> {
			try {
				processBookingDetail(bookingDetail, workflow, requestInfo);
			} catch (Exception e) {
				log.error("Failed to process booking number: {}. Error: {}", bookingDetail.getBookingNo(),
						e.getMessage(), e);
			}
		});
	}

	/**
	 * Processes the booking details by updating the workflow and booking status.
	 * 
	 * This method performs the following steps: 1. Updates the workflow state and
	 * retrieves the latest booking status. 2. Updates the booking status in the
	 * booking detail. 3. Persists the updated booking information in the
	 * repository.
	 * 
	 * @param bookingDetail The details of the community hall booking.
	 * @param workflow      The workflow instance associated with the booking.
	 * @param requestInfo   The request information containing user and transaction
	 *                      details.
	 */
	private void processBookingDetail(CommunityHallBookingDetail bookingDetail, ProcessInstance workflow,
			RequestInfo requestInfo) {
		log.info("Updating Workflow and status for booking number: {}", bookingDetail.getBookingNo());

		// Set workflow and build booking request
		bookingDetail.setWorkflow(workflow);
		CommunityHallBookingRequest bookingRequest = CommunityHallBookingRequest.builder()
				.hallsBookingApplication(bookingDetail).requestInfo(requestInfo).build();

		// Update workflow and booking status
		State state = workflowService.updateWorkflow(bookingRequest);
		bookingDetail.setBookingStatus(state.getApplicationStatus());

		// Persist updated booking
		bookingRepository.updateBooking(bookingRequest);
	}

}
