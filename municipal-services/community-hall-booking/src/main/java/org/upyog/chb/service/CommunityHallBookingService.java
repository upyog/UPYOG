package org.upyog.chb.service;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityResponse;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;

import digit.models.coremodels.PaymentDetail;
import lombok.NonNull;

public interface CommunityHallBookingService {

	CommunityHallBookingDetail createBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);
	
	CommunityHallBookingDetail createInitBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);	
	
	List<CommunityHallBookingDetail> getBookingDetails(CommunityHallBookingSearchCriteria bookingSearchCriteria, RequestInfo info);

	CommunityHallBookingDetail updateBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest, PaymentDetail paymentDetail, BookingStatusEnum bookingStatusEnum);

	CommunityHallSlotAvailabilityResponse getCommunityHallSlotAvailability(CommunityHallSlotSearchCriteria criteria, RequestInfo info);

	Integer getBookingCount(@Valid CommunityHallBookingSearchCriteria criteria, @NonNull RequestInfo requestInfo);

	/**
	 * We are updating booking status synchronously for updating booking status on payment success 
	 * Deleting the timer entry here after successful update of booking
	 * @param deleteBookingTimer 
	 */
	void updateBookingSynchronously(CommunityHallBookingRequest communityHallsBookingRequest,
			PaymentDetail paymentDetail, BookingStatusEnum status, boolean deleteBookingTimer);
	
}
