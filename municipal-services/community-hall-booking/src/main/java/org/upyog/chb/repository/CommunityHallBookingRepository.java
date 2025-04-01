package org.upyog.chb.repository;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.chb.web.models.BookingPaymentTimerDetails;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityDetail;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;

import digit.models.coremodels.PaymentDetail;

/**
 * This interface defines the contract for database operations related to the
 * Community Hall Booking module.
 * 
 * Purpose:
 * - To provide methods for creating, updating, retrieving, and deleting booking records.
 * - To handle slot availability and payment timer-related operations.
 * 
 * Methods:
 * 1. saveCommunityHallBooking:
 *    - Saves a new community hall booking record to the database.
 * 
 * 2. saveCommunityHallBookingInit:
 *    - Saves an initial booking record during the booking initialization process.
 * 
 * 3. getBookingDetails:
 *    - Retrieves booking details based on the provided search criteria.
 * 
 * 4. updateBooking:
 *    - Updates an existing booking record in the database.
 * 
 * 5. getCommunityHallSlotAvailability:
 *    - Retrieves slot availability details based on the provided search criteria.
 * 
 * 6. getBookingCount:
 *    - Returns the count of bookings matching the provided search criteria.
 * 
 * 7. createBookingTimer:
 *    - Creates a booking timer for payment or other time-sensitive operations.
 * 
 * 8. deleteBookingTimer:
 *    - Deletes a booking timer and optionally updates the booking status.
 * 
 * Usage:
 * - This interface is implemented by the repository layer to interact with the database.
 * - It ensures a consistent and reusable approach to database operations for the module.
 */
public interface CommunityHallBookingRepository {

	void saveCommunityHallBooking(CommunityHallBookingRequest bookingRequest);
	
	void saveCommunityHallBookingInit(CommunityHallBookingRequest bookingRequest);

	List<CommunityHallBookingDetail> getBookingDetails(CommunityHallBookingSearchCriteria bookingSearchCriteria);

	void updateBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);

	List<CommunityHallSlotAvailabilityDetail> getCommunityHallSlotAvailability(
			CommunityHallSlotSearchCriteria criteria);

	Integer getBookingCount(@Valid CommunityHallBookingSearchCriteria criteria);

	void createBookingTimer(CommunityHallSlotSearchCriteria criteria, RequestInfo requestInfo, boolean updateBookingStatus);

	void deleteBookingTimer(String bookingId, boolean updateBookingStatus);

	void updateBookingSynchronously(String bookingId, String uuid, PaymentDetail paymentDetail, String status);

	List<BookingPaymentTimerDetails> getBookingTimer(CommunityHallSlotSearchCriteria criteria);

	/**
	 * Updates the createdTime field for a given booking.
	  
	 */
	int updateBookingTimer(String bookingId);

	List<BookingPaymentTimerDetails> getExpiredBookingTimer();

	List<BookingPaymentTimerDetails> getBookingTimer(List<String> bookingIds);

	List<BookingPaymentTimerDetails> getBookingTimerByCreatedBy(RequestInfo info, CommunityHallSlotSearchCriteria criteria);

}