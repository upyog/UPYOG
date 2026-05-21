//package org.upyog.adv.scheduler;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.egov.common.contract.request.RequestInfo;
//import org.egov.common.contract.request.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.upyog.adv.service.AdvAutoEscalationService;
//import org.upyog.adv.service.BookingService;
//import org.upyog.adv.service.UserService;
//import org.upyog.adv.web.models.AdvertisementSearchCriteria;
//import org.upyog.adv.web.models.BookingRequest;
//import org.upyog.adv.web.models.BookingDetail;
//import org.upyog.adv.web.models.workflow.ProcessInstance;
//import org.upyog.adv.web.models.workflow.Workflow;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//
//@Component
//@EnableScheduling
//@Slf4j
//public class AdvAutoEscalationScheduler {
//
//    @Autowired
//    private AdvAutoEscalationService autoEscalationService;
//
//    @Autowired
//    private BookingService bookingService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private ObjectMapper mapper;
//
//    // SLAs in milliseconds: weekly, biweekly, monthly(30d), yearly(365d)
//    private static final List<Long> SLAS = Arrays.asList(
//            60L * 1000
////            7L * 24 * 3600 * 1000,        // weekly
////            14L * 24 * 3600 * 1000,       // biweekly
////            30L * 24 * 3600 * 1000,       // monthly (30d)
////            365L * 24 * 3600 * 1000       // yearly
//    );
//
//    @Scheduled(initialDelay = 1000, fixedRate = 60000)
//    public void autoEscalate() {
//        log.info("Start Advertisement Auto Escalation Scheduler...");
//
//        // Build RequestInfo using system user
//        RequestInfo requestInfo = new RequestInfo();
//        try {
//            org.upyog.adv.web.models.OwnerInfo owner = userService.searchSystemUser();
//            User user = mapper.convertValue(owner, User.class);
//            requestInfo.setUserInfo(user);
//        } catch (Exception ex) {
//            log.error("Failed to obtain system user for auto escalation: {}", ex.getMessage());
//            return;
//        }
//
//        // Fetch MDMS entries for AutoEscalation (we will reuse their action/comment but use our SLAs)
//        List<java.util.Map<String, Object>> mdmsData = autoEscalationService.fetchAutoEscalationMdmsData(requestInfo, "pb");
//
//        if (mdmsData == null || mdmsData.isEmpty()) {
//            log.info("No AutoEscalation MDMS rows found, skipping scheduler run.");
//            return;
//        }
//
//        for (Map<String, Object> mdmsRow : mdmsData) {
//            for (Long sla : SLAS) {
//                // copy mdms row and set our SLA override value
//                Map<String, Object> mdmsCopy = new HashMap<>(mdmsRow);
//                mdmsCopy.put("sla", sla);
//
//                List<ProcessInstance> instances = autoEscalationService.fetchAutoEscalationApplications(mdmsCopy, requestInfo);
//                if (instances == null || instances.isEmpty()) continue;
//                for (ProcessInstance pi : instances) {
//                    if (pi.getBusinessId() == null) continue;
//                    try {
//                        AdvertisementSearchCriteria criteria = new AdvertisementSearchCriteria();
//                        criteria.setBookingNo(pi.getBusinessId());
//                        criteria.setTenantId(pi.getTenantId());
//                        List<BookingDetail> bookings = bookingService.getBookingDetails(criteria, requestInfo);
//                        if (bookings == null || bookings.isEmpty()) continue;
//                        BookingDetail booking = bookings.get(0);
//
//                        // Use action/comment from ProcessInstance if present, otherwise prefer MDMS config
//                        String action = (pi.getAction() != null && !pi.getAction().isEmpty()) ? pi.getAction() : "PENDING_FOR_INSPECTION";
//                        String comment = (pi.getComment() != null && !pi.getComment().isEmpty()) ? pi.getComment() : "Auto-escalation by scheduler";
//                        if (mdmsCopy.get("action") != null) action = mdmsCopy.get("action").toString();
//                        if (mdmsCopy.get("comment") != null) comment = mdmsCopy.get("comment").toString();
//
//                        booking.setWorkflow(Workflow.builder().action(action).comment(comment).build());
//                        BookingRequest br = BookingRequest.builder().requestInfo(requestInfo).bookingApplication(booking).build();
//                        bookingService.updateBooking(br, null, null);
//                        log.info("Auto-escalated booking {} with action {}", booking.getBookingNo(), action);
//                    } catch (Exception ex) {
//                        log.error("Failed to auto-escalate businessId {}: {}", pi.getBusinessId(), ex.getMessage());
//                    }
//                }
//            }
//
//            log.info("End Advertisement Auto Escalation Scheduler.");
//        }
//    }
//}
