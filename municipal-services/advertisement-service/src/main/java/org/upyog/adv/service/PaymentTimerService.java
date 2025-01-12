package org.upyog.adv.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentTimerService {

	@Autowired
	@Lazy
	private BookingRepository bookingRepository;

//	@Autowired
//	private BookingConfiguration config;

	@Transactional
	public void insertBookingIdForTimer(List<AdvertisementSlotSearchCriteria> criteria, RequestInfo requestInfo,  List<AdvertisementSlotAvailabilityDetail> availabiltityDetailsResponse
			) {
		bookingRepository.insertBookingIdForTimer(criteria, requestInfo, availabiltityDetailsResponse.get(0));
		//long timerValue = config.getPaymentTimer();
		//log.info("Creating timer entry for booking id : {} with timer value : {}", criteria.getBookingId(), timerValue);
		
	}
	@Transactional
	public void deleteBookingIdForTimer(String bookingId, RequestInfo requestInfo) {
		log.info("Creating timer entry for booking id : {}", bookingId);
		bookingRepository.deleteBookingIdForTimer(bookingId);
		
	}
	

//	public void getRemainingTimerValue(List<BookingDetail> bookingDetails) {
//        Map<String, Long> remainingTimerValue = bookingRepository.getRemainingTimerValues(bookingDetails);   
//        log.info("Received Remaining Timers for bookingId: {}", remainingTimerValue);
//
//     	for (BookingDetail bookingDetail : bookingDetails) {
//     			Long remainingTimer = remainingTimerValue.get(bookingDetail.getBookingId());
//     			bookingDetail.setTimerValue(remainingTimer != null ? remainingTimer / 1000 : 0L);
//     		    log.info("Updated BookingDetail: {} with Remaining Timer: {}", bookingDetail.getBookingId(), remainingTimer);
//
//     	}
//	}


}
