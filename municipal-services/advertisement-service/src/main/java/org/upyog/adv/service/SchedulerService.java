package org.upyog.adv.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.adv.repository.BookingRepository;

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

	private final BookingRepository bookingRepo;

	public SchedulerService(BookingRepository bookingRepo) {
		this.bookingRepo = bookingRepo;
	}

	/**
	 * Deletes expired or failed payment timer entries on a fixed interval.
	 *
	 * <p>This scheduled task runs every 5 minutes and removes stale booking
	 * references from the payment timer table.</p>
	 */
	@Scheduled(fixedRate = 5 * 60 * 1000) //Runs every 5 minutes
	public void cleanupExpiredEntries() {
		bookingRepo.scheduleTimerDelete();
	}


}
