package org.upyog.chb.service;
import static org.upyog.chb.constants.CommunityHallBookingConstants.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.BookingPaymentTimerDetails;
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.VenueBookingRequest;
import org.upyog.chb.web.models.VenueBookingSearchCriteria;
import org.upyog.chb.web.models.workflow.ProcessInstance;
import org.upyog.chb.web.models.workflow.State;

import digit.models.coremodels.UserDetailResponse;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Slf4j
@Component
public class SchedulerService {

	private final CommunityHallBookingRepository bookingRepository;
	private final UserService userService;
	private final WorkflowService workflowService;
	private final CommunityHallBookingConfiguration config;
	private final SchedulerService self;

	public SchedulerService(CommunityHallBookingRepository bookingRepository, UserService userService,
			WorkflowService workflowService, CommunityHallBookingConfiguration config,
			@Lazy SchedulerService self) {
		this.bookingRepository = bookingRepository;
		this.userService = userService;
		this.workflowService = workflowService;
		this.config = config;
		this.self = self;
	}

	@Scheduled(fixedRate = 5 * 60 * 1000)
	@SchedulerLock(
		name = "chbCleanupExpiredEntriesJob",
		lockAtLeastFor = "PT1M",
		lockAtMostFor = "PT10M"
	)
	public void cleanupExpiredEntries() {
		log.info("Delete Expired Booking task running...:::.....:::");
		self.deleteExpiredBookings();
	}

	@Transactional
	public void deleteExpiredBookings() {
		List<BookingPaymentTimerDetails> bookingPaymentTimerDetails = bookingRepository.getExpiredBookingTimer();
		List<String> bookingList = bookingPaymentTimerDetails.stream().map(BookingPaymentTimerDetails::getBookingId)
				.collect(Collectors.toList());

		log.info("Expired booking id list  : {}", bookingList);

		String bookingIds = String.join(",", bookingList);

		bookingRepository.deleteBookingTimer(bookingIds, true);

	}

	@Scheduled(cron = "0 0 1 * * *")
	@SchedulerLock(
		name = "chbUpdateWorkflowForBookedApplicationsJob",
		lockAtLeastFor = "PT1M",
		lockAtMostFor = "PT30M"
	)
	public void updateWorkflowForBookedApplications() {
		log.info("Scheduler - Updating Workflow of Booked applications...");

		String formattedDate = CommunityHallBookingUtil.parseLocalDateToString(
				LocalDate.now(ZoneId.systemDefault()).minusDays(1), "yyyy-MM-dd");

		List<VenueBookingDetail> bookingDetails = bookingRepository.getBookingDetails(
				VenueBookingSearchCriteria.builder().toDate(formattedDate).status(CHB_STATUS_BOOKED).build());

		if (bookingDetails == null || bookingDetails.isEmpty()) {
			log.info("No booked applications found for update.");
			return;
		}
		String bookingNos = bookingDetails.stream().map(VenueBookingDetail::getBookingNo)
				.collect(Collectors.joining(", "));
		log.info("Booking Nos: " + bookingNos);

		UserDetailResponse userDetailResponse = userService.searchByUserName(config.getInternalMicroserviceUserName(), config.getStateLevelTenantId());
		if (userDetailResponse == null || userDetailResponse.getUser().isEmpty()) {
			throw new IllegalStateException("SYSTEM user not found for tenant '" + CHB_TENANTID + "'.");
		}

		RequestInfo requestInfo = RequestInfo.builder().userInfo(userDetailResponse.getUser().get(0)).build();

		ProcessInstance workflow = ProcessInstance.builder().action(CHB_ACTION_MOVETOEMPLOYEE)
				.moduleName(CHB_REFUND_WORKFLOW_MODULENAME).tenantId(CHB_TENANTID)
				.businessService(CHB_REFUND_WORKFLOW_BUSINESSSERVICE).build();

		bookingDetails.forEach(bookingDetail -> {
			try {
				processBookingDetail(bookingDetail, workflow, requestInfo);
			} catch (Exception e) {
				log.error("Failed to process booking number: {}. Error: {}", bookingDetail.getBookingNo(),
						e.getMessage(), e);
			}
		});
	}

	private void processBookingDetail(VenueBookingDetail bookingDetail, ProcessInstance workflow,
			RequestInfo requestInfo) {
		log.info("Updating Workflow and status for booking number: {}", bookingDetail.getBookingNo());

		bookingDetail.setWorkflow(workflow);
		VenueBookingRequest bookingRequest = VenueBookingRequest.builder()
				.venueBookingApplication(bookingDetail).requestInfo(requestInfo).build();

		State state = workflowService.updateWorkflow(bookingRequest);
		bookingDetail.setBookingStatus(state.getApplicationStatus());

		bookingRepository.updateBooking(bookingRequest);
	}

}
