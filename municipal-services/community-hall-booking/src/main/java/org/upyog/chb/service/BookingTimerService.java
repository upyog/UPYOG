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
	public void deleteBookingTimer(String bookingId) {
		log.info("Deleting timer entry for booking id : {}", bookingId);
		bookingRepository.deleteBookingTimer(bookingId, true);
	}

	public List<BookingPaymentTimerDetails> getBookingFromTimerTable(RequestInfo info, CommunityHallSlotSearchCriteria criteria) {
		List<BookingPaymentTimerDetails> bookingPaymentTimerDetails= bookingRepository.getBookingTimerByCreatedBy(info,criteria);
		return bookingPaymentTimerDetails;
	}
	
}
