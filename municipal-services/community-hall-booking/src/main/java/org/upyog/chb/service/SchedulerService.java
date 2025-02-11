package org.upyog.chb.service;

import static org.upyog.chb.constants.CommunityHallBookingConstants.CHB_ACTION_MOVETOEMPLOYEE;
import static org.upyog.chb.constants.CommunityHallBookingConstants.CHB_REFUND_BUSINESSSERVICE;
import static org.upyog.chb.constants.CommunityHallBookingConstants.CHB_REFUND_MODULENAME;
import static org.upyog.chb.constants.CommunityHallBookingConstants.CHB_STATUS_BOOKED;
import static org.upyog.chb.constants.CommunityHallBookingConstants.CHB_TENANTID;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.upyog.chb.enums.BookingStatusEnum;
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

@Slf4j
@Component
public class SchedulerService {

	@Autowired
	private CommunityHallBookingService communityHallBookingService;

	@Autowired
	private CommunityHallBookingRepository bookingRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private WorkflowService workflowService;

	/*
	 * This scheduler runs every 5 mins to delete the bookingId from the
	 * paymentTimer table when the timer is expired or payment is failed
	 */
	@Scheduled(fixedRate = 5 * 60 * 1000) // Runs every 5 minutes
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

//	@Scheduled(fixedRate = 10 * 60 * 1000)
	@Scheduled(cron = "0 0 1 * * *")
	public void updateWorkflowForBookedApplications() {
		log.info("Scheduler - Updating Workflow of Booked applications...");

		String formattedDate = CommunityHallBookingUtil.parseLocalDateToString(LocalDate.now().minusDays(1), "yyyy-MM-dd");

		List<CommunityHallBookingDetail> bookingDetails = bookingRepository.getBookingDetails(
				CommunityHallBookingSearchCriteria.builder().toDate(formattedDate).status(CHB_STATUS_BOOKED).build());
		
		
		// Exit if no booking details are found
		if (bookingDetails == null || bookingDetails.isEmpty()) {
			log.info("No booked applications found for update.");
			return;
		}else {
			String bookingNos = bookingDetails.stream()
			        .map(CommunityHallBookingDetail::getBookingNo)
			        .collect(Collectors.joining(", "));
			    log.info("Booking Nos: " + bookingNos);
		}
		UserDetailResponse userDetailResponse = userService.searchByUserName("9999009999", CHB_TENANTID);
		if (userDetailResponse == null || userDetailResponse.getUser().isEmpty()) {
			throw new IllegalStateException("SYSTEM user not found for tenant '" + CHB_TENANTID + "'.");
		}

		RequestInfo requestInfo = RequestInfo.builder().userInfo(userDetailResponse.getUser().get(0)).build();

		ProcessInstance workflow = ProcessInstance.builder().action(CHB_ACTION_MOVETOEMPLOYEE)
				.moduleName(CHB_REFUND_MODULENAME).tenantId(CHB_TENANTID).businessService(CHB_REFUND_BUSINESSSERVICE)
				.build();

		// Process each booking detail
		bookingDetails.forEach(bookingDetail -> {
		    try {
		        processBookingDetail(bookingDetail, workflow, requestInfo);
		    } catch (Exception e) {
		        log.error("Failed to process booking number: {}. Error: {}", bookingDetail.getBookingNo(), e.getMessage(), e);
		    }
		});
	}

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
