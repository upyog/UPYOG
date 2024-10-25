package org.upyog.adv.service;

import java.util.List;

import javax.validation.Valid;

import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;

public interface BookingService {

	BookingDetail createBooking(@Valid BookingRequest bookingRequest);
	
	List<AdvertisementSlotAvailabilityDetail> getAdvertisementSlotAvailability(AdvertisementSlotSearchCriteria criteria);


}
