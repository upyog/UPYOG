package org.upyog.chb.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
			long timeReamining = CommunityHallBookingUtil.calculateDifferenceInMinutes(CommunityHallBookingUtil.getCurrentTimestamp(), bookingPaymentTimerDetails.getCreatedTime());
			if(timeReamining < 1) {
				bookingRepository.deleteBookingTimer(bookingId);
			}
			bookingRepository.updateBookingTimer(bookingId, info.getUserInfo().getUuid());
		} else {
			bookingRepository.createBookingTimer(criteria, info);
		}
		log.info("Creating timer entry for booking id : {} with timer value : {}", criteria.getBookingId(), timerValue);
		
		return timerValue;
		
	}
	
	@Transactional
	public void deleteBookingTimer(String bookingId) {
		log.info("Deleting timer entry for booking id : {}", bookingId);
		bookingRepository.deleteBookingTimer(bookingId);
	}

}
