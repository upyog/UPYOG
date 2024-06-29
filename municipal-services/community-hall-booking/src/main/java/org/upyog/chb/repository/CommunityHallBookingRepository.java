package org.upyog.chb.repository;

import java.util.List;

import javax.validation.Valid;

import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallSlotAvailabiltityDetail;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;

public interface CommunityHallBookingRepository {

	void saveCommunityHallBooking(CommunityHallBookingRequest bookingRequest);
	
	void saveCommunityHallBookingInit(CommunityHallBookingRequest bookingRequest);

	List<CommunityHallBookingDetail> getBookingDetails(CommunityHallBookingSearchCriteria bookingSearchCriteria);

	void updateBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);

	List<CommunityHallSlotAvailabiltityDetail> getCommunityHallSlotAvailability(
			CommunityHallSlotSearchCriteria criteria);
}
