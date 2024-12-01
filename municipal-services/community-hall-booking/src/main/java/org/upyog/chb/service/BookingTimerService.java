package org.upyog.chb.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

@Service
@Slf4j
public class BookingTimerService {
	
	
	@Autowired
	private CommunityHallBookingRepository bookingRepository;
	
	@Autowired
	private CommunityHallBookingConfiguration bookingConfiguration;
	
	
	@Transactional
	public long getTimerValue(CommunityHallSlotSearchCriteria criteria, RequestInfo info, List<CommunityHallSlotAvailabilityDetail> availabilityDetailsList) {
		long timerValue = CommunityHallBookingUtil.getSeconds(Integer.parseInt(bookingConfiguration.getBookingPaymentTimerValue()));
		String bookingId = criteria.getBookingId();
		List<BookingPaymentTimerDetails> paymentTimerList = bookingRepository.getBookingTimer(criteria);
		if(paymentTimerList.size() > 0) {
			log.info("Timer for Booking id : {} exists." , bookingId);
			BookingPaymentTimerDetails bookingPaymentTimerDetails = paymentTimerList.get(0);
			long timeReamining = getTimerValue(bookingPaymentTimerDetails);
			if(timeReamining < 1) {
				bookingRepository.deleteBookingTimer(bookingId);
				bookingRepository.createBookingTimer(criteria, info);
				return timerValue;
			}
			bookingRepository.updateBookingTimer(bookingId, info.getUserInfo().getUuid());
		} else {
			bookingRepository.createBookingTimer(criteria, info);
		}
		log.info("Creating timer entry for booking id : {} with timer value : {}", criteria.getBookingId(), timerValue);
		
		return timerValue;
		
	}
	
	public Long getTimerValue(BookingPaymentTimerDetails bookingPaymentTimerDetails) {
		long timerValue = CommunityHallBookingUtil.getSeconds(Integer.parseInt(bookingConfiguration.getBookingPaymentTimerValue()));
		long timeReamining = CommunityHallBookingUtil.calculateDifferenceInSeconds(CommunityHallBookingUtil.getCurrentTimestamp(), bookingPaymentTimerDetails.getCreatedTime());
		
		return timerValue - timeReamining;
		
	}
	
	@Transactional
	public Map<String, Long> getTimerValue(List<String> bookingIds) {

		// Retrieve payment timer details from the repository
		List<BookingPaymentTimerDetails> paymentTimerDetails = bookingRepository.getBookingTimer(bookingIds);

		// Create a map of bookingId to calculated timer value in minutes
		Map<String, Long> bookingIdTimerValueMap = paymentTimerDetails.stream()
				.collect(Collectors.toMap(BookingPaymentTimerDetails::getBookingId, timer ->  getTimerValue(timer)));
		
		List<String> expiredBookingIds = bookingIdTimerValueMap.entrySet().stream()
		        .filter(entry -> entry.getValue() < 1) // Filter entries where timer value is less than 1
		        .map(Map.Entry::getKey) // Extract the bookingId (key)
		        .collect(Collectors.toList()); // Collect the filtered bookingIds into a list
		
		if(!CollectionUtils.isEmpty(expiredBookingIds)) {
			String bookingIdString = String.join(",", expiredBookingIds);
			bookingRepository.deleteBookingTimer(bookingIdString);
			expiredBookingIds.stream().forEach(bookingId  -> bookingIdTimerValueMap.remove(bookingId));
		}
		

		log.info("Expired booking id : " + expiredBookingIds);

		// Log the map of bookingId to timer values
		log.info("Booking ID to Timer Value Map: {}", bookingIdTimerValueMap);
		
		return bookingIdTimerValueMap;

	}
	
	@Transactional
	public void deleteBookingTimer(String bookingId) {
		log.info("Deleting timer entry for booking id : {}", bookingId);
		bookingRepository.deleteBookingTimer(bookingId);
	}
	
}
