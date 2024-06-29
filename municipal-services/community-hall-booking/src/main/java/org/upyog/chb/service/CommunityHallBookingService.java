package org.upyog.chb.service;

import java.util.List;

import javax.validation.Valid;

import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallSlotAvailabiltityDetail;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;

public interface CommunityHallBookingService {

	CommunityHallBookingDetail createBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);
	
	CommunityHallBookingDetail createInitBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);	
	
	List<CommunityHallBookingDetail> getBookingDetails(CommunityHallBookingSearchCriteria bookingSearchCriteria);

	CommunityHallBookingDetail updateBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);

	void updateBookingStatus(String bookingNo);

	List<CommunityHallSlotAvailabiltityDetail> getCommunityHallSlotAvailability(CommunityHallSlotSearchCriteria criteria);

}
