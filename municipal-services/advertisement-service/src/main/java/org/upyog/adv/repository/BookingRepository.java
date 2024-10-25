package org.upyog.adv.repository;

import java.util.List;

import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;
import org.upyog.adv.web.models.BookingRequest;

public interface BookingRepository {

	void saveBooking(BookingRequest bookingRequest);
	
	List<AdvertisementSlotAvailabilityDetail> getAdvertisementSlotAvailability(
			AdvertisementSlotSearchCriteria criteria);
	
	
}
