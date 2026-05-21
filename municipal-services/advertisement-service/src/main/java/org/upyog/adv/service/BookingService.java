package org.upyog.adv.service;

import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.egov.common.contract.request.RequestInfo;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.web.models.AdvertisementSearchCriteria;
import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;

import lombok.NonNull;
import org.upyog.adv.web.models.billing.PaymentDetail;

public interface BookingService {

	BookingDetail createBooking(@Valid BookingRequest bookingRequest) throws JsonProcessingException;
	
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

	public boolean setSlotBookedFlag(List<AdvertisementSlotAvailabilityDetail> details);

	List<AdvertisementSlotAvailabilityDetail> getAdvertisementSlotAvailability(
			List<AdvertisementSlotSearchCriteria> criteriaList, RequestInfo requestInfo);

	String getDraftId(List<AdvertisementSlotAvailabilityDetail> availabiltityDetailsResponse, RequestInfo requestInfo);

	/**
	 * Modify the cart for an existing booking: mark removed slots REMOVED, delete timers for removed slots,
	 * insert timers for newly added slots and persist cart changes.
	 */
	org.upyog.adv.web.models.BookingDetail modifyCartSlots(org.upyog.adv.web.models.BookingRequest bookingRequest);

}
