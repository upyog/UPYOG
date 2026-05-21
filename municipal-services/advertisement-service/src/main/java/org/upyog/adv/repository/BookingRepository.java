package org.upyog.adv.repository;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.adv.web.models.*;

import digit.models.coremodels.PaymentDetail;
import lombok.NonNull;

public interface BookingRepository {

	void saveBooking(BookingRequest bookingRequest);

	Integer getBookingCount (@Valid AdvertisementSearchCriteria criteria);

	List<BookingDetail> getBookingDetails(AdvertisementSearchCriteria bookingSearchCriteria);
	List<OwnerInfo> getOwnerByBookingId(String bookingId);
	List<AdvertisementSlotAvailabilityDetail> getAdvertisementSlotAvailability(
			AdvertisementSlotSearchCriteria criteria);
	
	void updateBooking(@Valid BookingRequest bookingRequest);
	
	void deleteBookingIdForTimer(String bookingId);

	//Map<String, Long> getRemainingTimerValues(List<BookingDetail> bookingDetails);
	
	void insertBookingIdForTimer(List<AdvertisementSlotSearchCriteria> criteria, RequestInfo requestInfo,
			AdvertisementSlotAvailabilityDetail availabiltityDetailsResponse);

	/**
	 * Insert timer entries for the given criteria but set booking_id to the provided ownerId.
	 * ownerId can be an actual booking_id (for existing bookings) or a draft id used as owner.
	 */
	void insertBookingIdForTimerWithOwner(List<AdvertisementSlotSearchCriteria> criteria, String BookingNo,String ownerId,
			RequestInfo requestInfo, AdvertisementSlotAvailabilityDetail availabiltityDetailsResponse);
	
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

	/**
	 * Mark the given cart slots as REMOVED (status) for the booking and create audit.
	 */
	void markCartSlotsRemoved(String bookingId, List<org.upyog.adv.web.models.CartDetail> removed, String modifiedBy,
			long modifiedTime);

	/**
	 * Delete timer entries for the provided ownerId (bookingId or draft id) and for the given list of slots.
	 */
	void deleteTimerEntriesForSlots(String ownerId, List<org.upyog.adv.web.models.CartDetail> slots);

	// Scheduler support: find bookings that completed their duration and move to verification
	List<String> findBookingsEligibleForVerification();

	void bulkUpdateBookingStatusById(List<String> bookingIds, String status, String lastModifiedBy);
	
}
