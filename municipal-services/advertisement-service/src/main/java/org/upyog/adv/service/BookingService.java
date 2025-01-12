package org.upyog.adv.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.web.models.AdvertisementSearchCriteria;
import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;

import digit.models.coremodels.PaymentDetail;
import lombok.NonNull;

public interface BookingService {

	BookingDetail createBooking(@Valid BookingRequest bookingRequest);
	
	List<AdvertisementSlotAvailabilityDetail> checkAdvertisementSlotAvailability(AdvertisementSlotSearchCriteria criteria, RequestInfo requestInfo) ;

	List<BookingDetail> getBookingDetails(AdvertisementSearchCriteria bookingSearchCriteria, RequestInfo info);
	Integer getBookingCount(@Valid AdvertisementSearchCriteria criteria, @NonNull RequestInfo requestInfo);
	
	BookingDetail updateBooking(@Valid BookingRequest bookingRequest, PaymentDetail paymentDetail, BookingStatusEnum bookingStatusEnum);
	
	BookingDetail updateBookingSynchronously(BookingRequest bookingRequest, PaymentDetail paymentDetail,
			BookingStatusEnum booked);

	BookingDetail createAdvertisementDraftApplication(BookingRequest bookingRequest);

	List<BookingDetail> getAdvertisementDraftApplicationDetails(@NonNull RequestInfo requestInfo,
			@Valid AdvertisementSearchCriteria criteria);
	
	public String deleteAdvertisementDraft(String draftId);

//	String getDraftId(List<AdvertisementSlotAvailabilityDetail> availabiltityDetailsResponse,
//			AdvertisementSlotSearchCriteria criteria, RequestInfo RequestInfo);
	
	public boolean setSlotBookedFlag(List<AdvertisementSlotAvailabilityDetail> details);

	List<AdvertisementSlotAvailabilityDetail> getAdvertisementSlotAvailability(
			List<AdvertisementSlotSearchCriteria> criteriaList, RequestInfo requestInfo);

}
