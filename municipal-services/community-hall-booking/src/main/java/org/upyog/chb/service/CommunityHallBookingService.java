package org.upyog.chb.service;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityDetail;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;

import digit.models.coremodels.PaymentDetail;

public interface CommunityHallBookingService {

	CommunityHallBookingDetail createBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);
	
	CommunityHallBookingDetail createInitBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);	
	
	List<CommunityHallBookingDetail> getBookingDetails(CommunityHallBookingSearchCriteria bookingSearchCriteria, RequestInfo info);

	CommunityHallBookingDetail updateBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest, PaymentDetail paymentDetail, BookingStatusEnum bookingStatusEnum);

	List<CommunityHallSlotAvailabilityDetail> getCommunityHallSlotAvailability(CommunityHallSlotSearchCriteria criteria);

}
