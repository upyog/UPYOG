package org.upyog.chb.service;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityResponse;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;

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

	CommunityHallBookingDetail createBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);
	
	CommunityHallBookingDetail createInitBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);	
	
	List<CommunityHallBookingDetail> getBookingDetails(CommunityHallBookingSearchCriteria bookingSearchCriteria, RequestInfo info);

	CommunityHallBookingDetail updateBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest, PaymentDetail paymentDetail, BookingStatusEnum bookingStatusEnum);

	CommunityHallSlotAvailabilityResponse getCommunityHallSlotAvailability(CommunityHallSlotSearchCriteria criteria, RequestInfo info);

	Integer getBookingCount(@Valid CommunityHallBookingSearchCriteria criteria, @NonNull RequestInfo requestInfo);

	/**
	 * We are updating booking status synchronously for updating booking status on payment success 
	 * Deleting the timer entry here after successful update of booking
	 * @param deleteBookingTimer 
	 */
	void updateBookingSynchronously(CommunityHallBookingRequest communityHallsBookingRequest,
			PaymentDetail paymentDetail, BookingStatusEnum status, boolean deleteBookingTimer);
	
}
