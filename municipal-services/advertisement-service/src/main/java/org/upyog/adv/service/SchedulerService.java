package org.upyog.adv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.enums.BookingStatusEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchedulerService {

	@Autowired
	private BookingRepository bookingRepo;

	/* This scheduler runs every 5 mins
	 * to delete the bookingId from the paymentTimer table when
	 * the timer is expired or payment is failed
	 */
	@Scheduled(fixedRate = 5 * 60 * 1000) //Runs every 5 minutes
	public void cleanupExpiredEntries() {
	log.info("[Scheduler] cleanupExpiredEntries tick");
		bookingRepo.scheduleTimerDelete();
	}

	/*
	 * Move bookings from BOOKED to PENDING_FOR_VERIFICATION when all their slot dates are over.
	 * Runs daily at 00:30 server time. Safe to re-run.
	 */
//	@Scheduled(cron = "0 */1 * * * *")
//	public void moveCompletedBookingsToVerification() {
//		try {
//			log.info("[Scheduler] moveCompletedBookingsToVerification tick");
//			java.util.List<String> eligible = bookingRepo.findBookingsEligibleForVerification();
//			if (eligible == null || eligible.isEmpty()) return;
//			bookingRepo.bulkUpdateBookingStatusById(eligible, BookingStatusEnum.PENDING_FOR_VERIFICATION.toString(), "system");
//			log.info("Moved {} bookings to PENDING_FOR_VERIFICATION", eligible.size());
//		} catch (Exception e) {
//			log.error("Error moving bookings to PENDING_FOR_VERIFICATION", e);
//		}
//	}


}
