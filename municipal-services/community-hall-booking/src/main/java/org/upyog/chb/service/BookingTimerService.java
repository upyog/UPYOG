package org.upyog.chb.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
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
		// Defensive validation to avoid NPEs and make failures actionable
		validateTimerRequest(criteria, info);
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
    
	/**
	 * Ensures required fields for timer creation are present and well-formed.
	 */
	private void validateTimerRequest(CommunityHallSlotSearchCriteria criteria, RequestInfo info) {
		if (criteria == null) {
			throw new CustomException("INVALID_TIMER_REQUEST", "Slot search criteria cannot be null when creating timer");
		}
		if (info == null || info.getUserInfo() == null || info.getUserInfo().getUuid() == null || info.getUserInfo().getUuid().isEmpty()) {
			throw new CustomException("INVALID_REQUEST_INFO", "RequestInfo.userInfo.uuid is required to create a payment timer");
		}
		if (criteria.getBookingId() == null || criteria.getBookingId().isEmpty()) {
			throw new CustomException("MISSING_BOOKING_ID", "bookingId is required to create a payment timer");
		}
		if (criteria.getTenantId() == null || criteria.getTenantId().isEmpty()) {
			throw new CustomException("MISSING_TENANT", "tenantId is required to create a payment timer");
		}
		if (criteria.getCommunityHallCode() == null || criteria.getCommunityHallCode().isEmpty()) {
			throw new CustomException("MISSING_COMMUNITY_HALL_CODE", "communityHallCode is required to create a payment timer");
		}
		// Timer is created only for specific hallCode (not hallCodes list)
		if (criteria.getHallCode() == null || criteria.getHallCode().isEmpty()) {
			throw new CustomException("MISSING_HALL_CODE", "hallCode is required to create a payment timer");
		}
		if (criteria.getBookingStartDate() == null || criteria.getBookingStartDate().isEmpty()) {
			throw new CustomException("MISSING_BOOKING_START_DATE", "bookingStartDate (yyyy-MM-dd) is required to create a payment timer");
		}
		if (criteria.getBookingEndDate() == null || criteria.getBookingEndDate().isEmpty()) {
			throw new CustomException("MISSING_BOOKING_END_DATE", "bookingEndDate (yyyy-MM-dd) is required to create a payment timer");
		}
		// Basic format sanity check to fail fast if date is malformed
		try {
			CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingStartDate());
			CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingEndDate());
		} catch (Exception e) {
			throw new CustomException("INVALID_BOOKING_DATE_FORMAT", "bookingStartDate/bookingEndDate should be in yyyy-MM-dd format");
		}
	}
	
}
