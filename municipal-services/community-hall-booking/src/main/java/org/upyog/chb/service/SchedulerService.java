package org.upyog.chb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.chb.repository.CommunityHallBookingRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchedulerService {
	
	
	@Autowired
	private CommunityHallBookingRepository bookingRepository;
	
	
	/* This scheduler runs every 5 mins
	 * to delete the bookingId from the paymentTimer table when 
	 * the timer is expired or payment is failed
	 */
	//@Scheduled(fixedRate = 5 * 60 * 1000) //Runs every 5 minutes
	public void cleanupExpiredEntries() {


	    int rowsDeleted = bookingRepository.deleteExpiredBookingTimer();

	    log.info(rowsDeleted + " expired entries deleted.");
	}

	



}
