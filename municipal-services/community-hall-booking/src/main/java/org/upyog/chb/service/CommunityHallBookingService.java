package org.upyog.chb.service;

import java.util.List;

import jakarta.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.VenueBookingRequest;
import org.upyog.chb.web.models.VenueBookingSearchCriteria;
import org.upyog.chb.web.models.VenueSlotAvailabilityResponse;
import org.upyog.chb.web.models.VenueSlotSearchCriteria;

import digit.models.coremodels.PaymentDetail;
import lombok.NonNull;

/**
 * This interface defines the contract for service-level operations related to the
 * Community Hall Booking module.
 * 
 * Purpose:
 * - To provide methods for creating, updating, retrieving, and managing community hall bookings.
 * - To handle slot availability checks and booking status updates.
 * 
 * Methods:
 * 1. createBooking:
 *    - Creates a new community hall booking.
 *    - Validates and processes the booking request before saving it.
 * 
 * 2. createInitBooking:
 *    - Creates an initial booking during the booking initialization process.
 *    - Saves partial booking details for further processing.
 * 
 * 3. getBookingDetails:
 *    - Retrieves booking details based on the provided search criteria.
 *    - Supports filtering and pagination for efficient data retrieval.
 * 
 * 4. updateBooking:
 *    - Updates an existing booking with new details or status.
 *    - Handles payment details and updates the booking status accordingly.
 * 
 * 5. getCommunityHallSlotAvailability:
 *    - Checks the availability of slots for community halls based on the search criteria.
 *    - Returns a response containing available slots and their details.
 * 
 * 6. getBookingCount:
 *    - Returns the count of bookings matching the provided search criteria.
 *    - Useful for reporting and analytics purposes.
 * 
 * Usage:
 * - This interface is implemented by the service layer to provide business logic for the module.
 * - It ensures consistent and reusable service-level operations for community hall bookings.
 */
public interface CommunityHallBookingService {

/**
	 * Creates a new community hall booking with the provided request payload.
	 *
	 * <p>
	 * This method handles validation, enrichment, and persistence of the booking.
	 * If a timer hold was active prior to booking creation, it also reconciles the
	 * final booking id with any existing timer rows.
	 * </p>
	 *
	 * @param communityHallsBookingRequest booking request containing booking details and request metadata
	 * @return created booking detail
	 */
	VenueBookingDetail createBooking(@Valid VenueBookingRequest communityHallsBookingRequest);
	
	/**
	 * Creates an initial booking during the early booking flow.
	 *
	 * @param communityHallsBookingRequest initial booking request payload
	 * @return created booking detail with initial state
	 */
	VenueBookingDetail createInitBooking(@Valid VenueBookingRequest communityHallsBookingRequest);	

	/**
	 * Retrieves booking details matching the provided search criteria.
	 *
	 * @param bookingSearchCriteria criteria used to filter bookings
	 * @param info                  request metadata and user details
	 * @return matching booking details
	 */
	List<VenueBookingDetail> getBookingDetails(VenueBookingSearchCriteria bookingSearchCriteria, RequestInfo info);

	/**
	 * Updates an existing booking based on the provided request and payment details.
	 *
	 * @param communityHallsBookingRequest booking request containing updated data
	 * @param paymentDetail               payment information associated with the booking update
	 * @param bookingStatusEnum           target booking status
	 * @return updated booking detail
	 */
	VenueBookingDetail updateBooking(@Valid VenueBookingRequest communityHallsBookingRequest, PaymentDetail paymentDetail, BookingStatusEnum bookingStatusEnum);

	/**
	 * Gets community hall slot availability for the requested criteria.
	 *
	 * <p>
	 * This method evaluates existing bookings, active timer holds, and availability
	 * rules to return slot details and a possible payment timer value for the
	 * current request.
	 * </p>
	 *
	 * @param criteria slot search criteria containing hall codes, dates, and timer flags
	 * @param info     request metadata and authenticated user details
	 * @return response with available slots, booking statuses, and timer information
	 */
	VenueSlotAvailabilityResponse getCommunityHallSlotAvailability(VenueSlotSearchCriteria criteria, RequestInfo info);

	/**
	 * Returns the number of bookings matching the provided search criteria.
	 *
	 * @param criteria    booking search criteria
	 * @param requestInfo request metadata and user details
	 * @return count of matching bookings
	 */
	Integer getBookingCount(@Valid VenueBookingSearchCriteria criteria, @NonNull RequestInfo requestInfo);

	/**
	 * 
	 * Updates booking status synchronously and optionally deletes the associated
	 * timer entry.
	 *
	 * @param communityHallsBookingRequest booking request to update
	 * @param paymentDetail                optional payment details for the update
	 * @param status                       booking status to set
	 * @param deleteBookingTimer           whether to delete the timer entry after
	 *                                     update
	 */
	void updateBookingSynchronously(VenueBookingRequest communityHallsBookingRequest,
			PaymentDetail paymentDetail, BookingStatusEnum status, boolean deleteBookingTimer);
	
}
