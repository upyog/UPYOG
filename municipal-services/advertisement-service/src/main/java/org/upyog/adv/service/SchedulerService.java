package org.upyog.adv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.repository.querybuilder.AdvertisementBookingQueryBuilder;

import lombok.extern.slf4j.Slf4j;
/**
 * Scheduler service for handling periodic tasks in the Advertisement Booking Service.
 * 
 * Key Responsibilities:
 * - Periodically cleans up expired or failed payment timer entries from the database.
 * 
 * Dependencies:
 * - BookingRepository: Interacts with the database to perform cleanup operations.
 * 
 * Methods:
 * - `cleanupExpiredEntries`: Scheduled method that runs every 5 minutes to delete expired or failed payment timer entries.
 * 
 * Annotations:
 * - @Component: Marks this class as a Spring-managed component.
 * - @Slf4j: Enables logging for debugging and monitoring scheduled tasks.
 * - @Scheduled: Configures the method to run at a fixed interval (every 5 minutes).
 */
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
		bookingRepo.scheduleTimerDelete();
	}


}
