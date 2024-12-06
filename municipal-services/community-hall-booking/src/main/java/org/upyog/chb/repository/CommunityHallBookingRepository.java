package org.upyog.chb.repository;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.chb.web.models.BookingPaymentTimerDetails;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityDetail;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;

import digit.models.coremodels.PaymentDetail;

public interface CommunityHallBookingRepository {

	void saveCommunityHallBooking(CommunityHallBookingRequest bookingRequest);
	
	void saveCommunityHallBookingInit(CommunityHallBookingRequest bookingRequest);

	List<CommunityHallBookingDetail> getBookingDetails(CommunityHallBookingSearchCriteria bookingSearchCriteria);

	void updateBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);

	List<CommunityHallSlotAvailabilityDetail> getCommunityHallSlotAvailability(
			CommunityHallSlotSearchCriteria criteria);

	Integer getBookingCount(@Valid CommunityHallBookingSearchCriteria criteria);

	void createBookingTimer(CommunityHallSlotSearchCriteria criteria, RequestInfo requestInfo, boolean updateBookingStatus);

	void deleteBookingTimer(String bookingId, boolean updateBookingStatus);

	void updateBookingSynchronously(String bookingId, String uuid, PaymentDetail paymentDetail, String status);

	List<BookingPaymentTimerDetails> getBookingTimer(CommunityHallSlotSearchCriteria criteria);

	/**
	 * Updates the createdTime field for a given booking.
	  
	 */
	int updateBookingTimer(String bookingId);

	List<BookingPaymentTimerDetails> getExpiredBookingTimer();

	List<BookingPaymentTimerDetails> getBookingTimer(List<String> bookingIds);

}