package org.upyog.adv.service;

import java.util.List;

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
/**
 * Interface for the Booking Service in the Advertisement Booking module.
 * 
 * Key Responsibilities:
 * - Handles the creation, update, and retrieval of advertisement bookings.
 * - Manages draft applications and slot availability for advertisements.
 * - Provides methods for checking slot availability and managing booking statuses.
 * 
 * Methods:
 * - `createBooking`: Creates a new advertisement booking.
 * - `checkAdvertisementSlotAvailability`: Checks the availability of advertisement slots.
 * - `getBookingDetails`: Retrieves booking details based on search criteria.
 * - `getBookingCount`: Returns the count of bookings matching the search criteria.
 * - `updateBooking`: Updates an existing booking with payment details and status.
 * - `updateBookingSynchronously`: Updates a booking synchronously with status and payment details.
 * - `createAdvertisementDraftApplication`: Creates a draft application for advertisement booking.
 * - `getAdvertisementDraftApplicationDetails`: Retrieves details of draft applications.
 * - `deleteAdvertisementDraft`: Deletes a draft application by its ID.
 * - `setSlotBookedFlag`: Marks slots as booked based on availability details.
 * - `getAdvertisementSlotAvailability`: Retrieves slot availability details for advertisements.
 * - `getDraftId`: Generates a draft ID for advertisement slot availability.
 */
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

	public boolean setSlotBookedFlag(List<AdvertisementSlotAvailabilityDetail> details);

	List<AdvertisementSlotAvailabilityDetail> getAdvertisementSlotAvailability(
			List<AdvertisementSlotSearchCriteria> criteriaList, RequestInfo requestInfo);

	String getDraftId(List<AdvertisementSlotAvailabilityDetail> availabiltityDetailsResponse, RequestInfo requestInfo);

}
