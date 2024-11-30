package org.upyog.chb.repository;

import java.util.List;
import java.util.Optional;

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

	void createBookingTimer(CommunityHallSlotSearchCriteria criteria, RequestInfo requestInfo);

	void deleteBookingTimer(String bookingId);

	void updateBookingSynchronously(CommunityHallBookingRequest communityHallsBookingRequest, PaymentDetail paymentDetai, String status);

	List<BookingPaymentTimerDetails> getBookingTimer(CommunityHallSlotSearchCriteria criteria);

	/**
	 * Updates the lastModifiedBy and lastModifiedTime fields for a given booking.
	  
	 */
	int updateBookingTimer(String bookingId, String lastModifiedBy);

	int deleteExpiredBookingTimer();
}
