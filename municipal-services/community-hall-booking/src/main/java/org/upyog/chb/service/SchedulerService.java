package org.upyog.chb.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.web.models.BookingPaymentTimerDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchedulerService {

	@Autowired
	private CommunityHallBookingRepository bookingRepository;

	/*
	 * This scheduler runs every 5 mins to delete the bookingId from the
	 * paymentTimer table when the timer is expired or payment is failed
	 */
	@Scheduled(fixedRate = 5 * 60 * 1000) // Runs every 5 minutes
	public void cleanupExpiredEntries() {
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

}
