package org.upyog.chb.service;

import java.util.List;

import javax.validation.Valid;

import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;

public interface CommunityHallBookingService {

	CommunityHallBookingRequest createBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);
	
	CommunityHallBookingRequest createInitBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);	
	
	List<CommunityHallBookingDetail> getBookingDetails(CommunityHallBookingSearchCriteria bookingSearchCriteria);

}
