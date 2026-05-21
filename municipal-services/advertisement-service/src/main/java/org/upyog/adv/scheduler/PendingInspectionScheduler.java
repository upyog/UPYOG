package org.upyog.adv.scheduler;

import java.util.Arrays;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.service.BookingService;
import org.upyog.adv.service.UserService;
import org.upyog.adv.web.models.AdvertisementSearchCriteria;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.workflow.Workflow;
import org.upyog.adv.web.models.OwnerInfo;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Scheduler that uses the repository "query path" to find bookings eligible for
 * pending-inspection and moves them via workflow. Falls back to DB update if
 * workflow integration fails.
 *
 * This follows the plan documented in docs/scheduler-pending-inspection-plan.md
 */
@Component
@Slf4j
public class PendingInspectionScheduler {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;



    @Autowired
    private ObjectMapper mapper;

    // Run daily at 00:30 server time (adjust cron as needed)
    @Scheduled(initialDelay = 10000, fixedRate = 60000)
    public void runPendingInspectionScheduler() {
        log.info("[Scheduler] PendingInspectionScheduler tick");

        List<String> eligibleBookingIds = null;
        try {
            eligibleBookingIds = bookingRepository.findBookingsEligibleForVerification();
        } catch (Exception ex) {
            log.error("Failed to fetch eligible bookings from repository: {}", ex.getMessage(), ex);
            return;
        }

        if (eligibleBookingIds == null || eligibleBookingIds.isEmpty()) {
            log.info("No eligible bookings found for Pending-for-Inspection");
            return;
        }

        // Build RequestInfo using system user
        RequestInfo requestInfo = new RequestInfo();
        String systemUserUuid = "system";
        try {
            OwnerInfo owner = userService.searchSystemUser();
            User user = mapper.convertValue(owner, User.class);
            requestInfo.setUserInfo(user);
            if (owner != null && owner.getUuid() != null) systemUserUuid = owner.getUuid();
        } catch (Exception ex) {
            log.error("Failed to obtain system user for PendingInspectionScheduler: {}", ex.getMessage(), ex);
            // continue with a minimal requestInfo (some WF setups accept it)
        }

        for (String bookingno : eligibleBookingIds) {
            try {
                AdvertisementSearchCriteria criteria = new AdvertisementSearchCriteria();
                criteria.setBookingNo(bookingno);
                List<BookingDetail> bookings = bookingService.getBookingDetails(criteria, requestInfo);
                if (bookings == null || bookings.isEmpty()) {
                    log.warn("No booking details found for bookingId {} — skipping", bookingno);
                    continue;
                }
                BookingDetail booking = bookings.get(0);

                // Use same update flow as AdvAutoEscalationScheduler: set workflow action/comment on booking
                try {
                    String action = "PENDING_FOR_INSPECTION";
                    String comment = "Auto-escalation by scheduler";

                    booking.setWorkflow(Workflow.builder().action(action).comment(comment).build());
                    BookingRequest br = BookingRequest.builder().requestInfo(requestInfo).bookingApplication(booking).build();
                    bookingService.updateBooking(br, null, null);
                    log.info("PendingInspectionScheduler: auto-escalated booking {} with action {}", booking.getBookingNo(), action);
                } catch (Exception ex) {
                    log.error("Failed to auto-escalate booking {} via bookingService.updateBooking: {}", booking.getBookingNo(), ex.getMessage(), ex);
                }
            } catch (Exception ex) {
                log.error("Failed processing bookingId {}: {}", bookingno, ex.getMessage(), ex);
            }
        }

        log.info("[Scheduler] PendingInspectionScheduler completed");
    }

}
