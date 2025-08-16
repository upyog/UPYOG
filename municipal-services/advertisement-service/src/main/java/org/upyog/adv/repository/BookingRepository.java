package org.upyog.adv.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.adv.web.models.AdvertisementDraftDetail;
import org.upyog.adv.web.models.AdvertisementSearchCriteria;
import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;

import digit.models.coremodels.PaymentDetail;
import lombok.NonNull;
/**
 * Interface for the Booking Repository in the Advertisement Booking Service.
 * 
 * This interface defines methods for interacting with the database to manage
 * advertisement bookings, draft applications, and slot availability.
 * 
 * Key Responsibilities:
 * - Save and update booking details
 * - Fetch booking details and counts based on search criteria
 * - Manage draft applications for advertisement bookings
 * - Handle slot availability and timer-related operations
 * 
 * Methods:
 * - `saveBooking`: Saves a new booking into the database
 * - `updateBooking`: Updates an existing booking
 * - `getBookingDetails`: Fetches booking details based on search criteria
 * - `getAdvertisementSlotAvailability`: Retrieves slot availability details
 * - `saveDraftApplication`: Saves a draft application
 * - `updateDraftApplication`: Updates an existing draft application
 * - `deleteBookingIdForTimer`: Deletes a booking ID from the timer table
 * - `insertBookingIdForTimer`: Inserts a booking ID into the timer table
 * - `getRemainingTimerValues`: Retrieves remaining timer values for a booking
 * 
 * This interface is implemented by the `BookingRepositoryImpl` class.
 */
public interface BookingRepository {

	void saveBooking(BookingRequest bookingRequest);

	Integer getBookingCount (@Valid AdvertisementSearchCriteria criteria);

	List<BookingDetail> getBookingDetails(AdvertisementSearchCriteria bookingSearchCriteria);
	List<AdvertisementSlotAvailabilityDetail> getAdvertisementSlotAvailability(
			AdvertisementSlotSearchCriteria criteria);
	
	void updateBooking(@Valid BookingRequest bookingRequest);
	
	void deleteBookingIdForTimer(String bookingId);

	//Map<String, Long> getRemainingTimerValues(List<BookingDetail> bookingDetails);
	
	void insertBookingIdForTimer(List<AdvertisementSlotSearchCriteria> criteria, RequestInfo requestInfo,
			AdvertisementSlotAvailabilityDetail availabiltityDetailsResponse);
	
	Map<String, Long> getRemainingTimerValues(String bookingId);
	
	void saveDraftApplication(BookingRequest bookingRequest);

	void updateDraftApplication(BookingRequest bookingRequest);

	List<BookingDetail> getAdvertisementDraftApplications(@NonNull RequestInfo requestInfo,
			@Valid AdvertisementSearchCriteria advertisementSearchCriteria);

	void deleteDraftApplication(String draftId);

	void scheduleTimerDelete();

	void updateBookingSynchronously(String bookingId, String uuid, PaymentDetail paymentDetail, String status);

	void updateBookingSynchronously(BookingRequest advertisementBookingRequest);

	List<AdvertisementDraftDetail> getDraftData(String uuid);

	List<AdvertisementSlotAvailabilityDetail> getBookedSlotsFromTimer(AdvertisementSlotSearchCriteria criteria, RequestInfo requestInfo);

	void updateTimerBookingId(String bookingId, String bookingNo, String draftIdFromDraft);

	void updateStatusForTimer(String bookingNo, String status);

	List<AdvertisementSlotAvailabilityDetail> getBookedSlots(AdvertisementSlotSearchCriteria criteria,
			RequestInfo requestInfo);

	void getTimerData(String draftId, AdvertisementSlotSearchCriteria criteria, RequestInfo requestInfo,
			AdvertisementSlotAvailabilityDetail detail,  List<AdvertisementSlotSearchCriteria> criteriaList);
	
	void getAndInsertTimerData(String draftId, 
            List<AdvertisementSlotSearchCriteria> criteriaList, 
            RequestInfo requestInfo, 
            AdvertisementSlotAvailabilityDetail availabilityDetailsResponse);

	void deleteDataFromTimerAndDraft(String uuid, String draftId, String bookingId);
	
}
