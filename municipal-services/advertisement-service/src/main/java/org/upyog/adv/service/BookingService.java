package org.upyog.adv.service;

import java.util.List;

import javax.validation.Valid;

import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;
import org.egov.common.contract.request.RequestInfo;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.AdvertisementSearchCriteria;

import lombok.NonNull;

public interface BookingService {

	BookingDetail createBooking(@Valid BookingRequest bookingRequest);
	
	List<AdvertisementSlotAvailabilityDetail> getAdvertisementSlotAvailability(AdvertisementSlotSearchCriteria criteria);

	List<BookingDetail> getBookingDetails(AdvertisementSearchCriteria bookingSearchCriteria, RequestInfo info);
	Integer getBookingCount(@Valid AdvertisementSearchCriteria criteria, @NonNull RequestInfo requestInfo);
}
