package org.upyog.adv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.repository.querybuilder.AdvertisementBookingQueryBuilder;
import org.upyog.adv.util.BookingUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchedulerService {
	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private AdvertisementBookingQueryBuilder queryBuilder;
	
	@Autowired
	private BookingConfiguration bookingConfiguration;
	
	/* This scheduler runs every 5 mins
	 * to delete the bookingId from the paymentTimer table when 
	 * the timer is expired or payment is failed
	 */
	@Scheduled(fixedRate = 5 * 60 * 1000) //Runs every 5 minutes
	public void cleanupExpiredEntries() {

	    long currentTimeMillis = BookingUtil.getCurrentTimestamp();
	    
	    String query = queryBuilder.deleteBookingIdPaymentTimer();

	    int rowsDeleted = jdbcTemplate.update(query, currentTimeMillis, bookingConfiguration.getPaymentTimer());

	    log.info(rowsDeleted + " expired entries deleted.");
	}

	



}
