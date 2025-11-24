package org.upyog.chb.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.BookingPaymentTimerDetails;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityDetail;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;

import lombok.extern.slf4j.Slf4j;

/**
 * This service class handles operations related to booking timers in the
 * Community Hall Booking module.
 * 
 * Purpose:
 * - To manage timer-related operations for bookings, such as retrieving and updating timer values.
 * - To ensure that bookings adhere to time-sensitive constraints, such as payment deadlines.
 * 
 * Dependencies:
 * - CommunityHallBookingRepository: Handles database operations for booking timers.
 * - CommunityHallBookingConfiguration: Provides configuration properties for timer operations.
 * - CommunityHallBookingUtil: Utility class for common operations related to bookings.
 * 
 * Features:
 * - Retrieves timer values for bookings based on search criteria.
 * - Updates timer-related details in the database.
 * - Ensures transactional consistency for timer-related operations.
 * - Logs important operations and errors for debugging and monitoring purposes.
 * 
 * Methods:
 * 1. getTimerValue:
 *    - Retrieves the timer value for a booking based on the provided search criteria.
 *    - Uses the availability details list to calculate or validate the timer value.
 * 
 * Usage:
 * - This class is automatically managed by Spring and injected wherever timer-related
 *   operations are required.
 * - It ensures consistent and reusable logic for managing booking timers.
 */
@Service
@Slf4j
public class BookingTimerService {
	
	
	@Autowired
	private CommunityHallBookingRepository bookingRepository;
	
	@Autowired
	private CommunityHallBookingConfiguration bookingConfiguration;
	
	
	@Transactional
	public long getTimerValue(CommunityHallSlotSearchCriteria criteria, RequestInfo info, List<CommunityHallSlotAvailabilityDetail> availabilityDetailsList) {
		long timerValue = 0 ;
		String bookingId = criteria.getBookingId();
		List<BookingPaymentTimerDetails> paymentTimerList = bookingRepository.getBookingTimer(criteria);
		if(!CollectionUtils.isEmpty(paymentTimerList)) {
			log.info("Timer for Booking id : {} exists." , bookingId);
			BookingPaymentTimerDetails bookingPaymentTimerDetails = paymentTimerList.get(0);
			long timeReamining = getTimerValue(bookingPaymentTimerDetails);
			if(timeReamining < 1) {
				//return 0 need to reload timer on front end
				return timerValue;
			}else {
				log.info("Timer for Booking id : {} exists with value  : {}" , bookingId, timerValue);
				//if existing timer not expired return value from here
				return timeReamining;
			}
			
		} else {
			bookingRepository.createBookingTimer(criteria, info, true);
		}
		timerValue = CommunityHallBookingUtil.getSeconds(Integer.parseInt(bookingConfiguration.getBookingPaymentTimerValue()));
		log.info("Creating timer entry for booking id : {} with timer value : {}", criteria.getBookingId(), timerValue);
		return timerValue;
		
	}
	
	public Long getTimerValue(BookingPaymentTimerDetails bookingPaymentTimerDetails) {
		long timerValue = CommunityHallBookingUtil.getSeconds(Integer.parseInt(bookingConfiguration.getBookingPaymentTimerValue()));
		long currentTimestamp = CommunityHallBookingUtil.getCurrentTimestamp();
		long timeSpentAfterCreation = CommunityHallBookingUtil.calculateDifferenceInSeconds(currentTimestamp, bookingPaymentTimerDetails.getCreatedTime());
		log.info("currentTimestamp : {} createdTime : {} timeSpentAfterCreation : {} ", currentTimestamp, bookingPaymentTimerDetails.getCreatedTime(), timeSpentAfterCreation);
		return timerValue - timeSpentAfterCreation;
		
	}
	
	@Transactional
	public void deleteBookingTimer(String bookingId, boolean updateBookingStatus) {
		log.info("Deleting timer entry for booking id : {}", bookingId);
		bookingRepository.deleteBookingTimer(bookingId, updateBookingStatus);
	}

	public List<BookingPaymentTimerDetails> getBookingFromTimerTable(RequestInfo info, CommunityHallSlotSearchCriteria criteria) {
		List<BookingPaymentTimerDetails> bookingPaymentTimerDetails= bookingRepository.getBookingTimerByCreatedBy(info,criteria);
		return bookingPaymentTimerDetails;
	}
	
}
