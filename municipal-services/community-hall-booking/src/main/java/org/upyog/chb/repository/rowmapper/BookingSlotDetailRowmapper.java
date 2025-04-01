package org.upyog.chb.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.BookingSlotDetail;

/**
 * This class is responsible for mapping the result set from the database to a list of
 * BookingSlotDetail objects.
 * 
 * Purpose:
 * - To extract data from the ResultSet and populate BookingSlotDetail objects.
 * - To handle the mapping of database fields to the corresponding fields in the BookingSlotDetail model.
 * 
 * Features:
 * - Implements the ResultSetExtractor interface to process the ResultSet.
 * - Iterates through the ResultSet and maps each row to a BookingSlotDetail object.
 * - Uses utility methods from CommunityHallBookingUtil for parsing and audit details.
 * 
 * Dependencies:
 * - CommunityHallBookingUtil: Provides utility methods for parsing dates and extracting audit details.
 * - BookingSlotDetail: The model class representing slot details for a booking.
 * 
 * Fields Mapped:
 * - slot_id: Maps to the slotId field in BookingSlotDetail.
 * - booking_id: Maps to the bookingId field in BookingSlotDetail.
 * - hall_code: Maps to the hallCode field in BookingSlotDetail.
 * - booking_date: Maps to the bookingDate field in BookingSlotDetail.
 * - booking_from_time: Maps to the bookingFromTime field in BookingSlotDetail.
 * - booking_to_time: Maps to the bookingToTime field in BookingSlotDetail.
 * - status: Maps to the status field in BookingSlotDetail.
 * - capacity: Maps to the capacity field in BookingSlotDetail.
 * - Audit details: Extracted using CommunityHallBookingUtil and mapped to the auditDetails field.
 * 
 * Usage:
 * - This class is used by the repository layer to map database query results to BookingSlotDetail objects.
 * - It ensures consistency and reusability of mapping logic across the application.
 */
@Component
public class BookingSlotDetailRowmapper implements ResultSetExtractor<List<BookingSlotDetail>> {

	@Override
	public List<BookingSlotDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<BookingSlotDetail> bookingSlotDetails = new ArrayList<>();
		while (rs.next()) {
			

			BookingSlotDetail slotDetail = BookingSlotDetail.builder().slotId(rs.getString("slot_id"))
					.bookingId(rs.getString("booking_id")).hallCode(rs.getString("hall_code"))
					.bookingDate(CommunityHallBookingUtil.parseStringToLocalDate(rs.getString("booking_date")))
					.bookingFromTime(LocalTime.parse(rs.getString("booking_from_time")))
					.bookingToTime(LocalTime.parse(rs.getString("booking_to_time")))
					.status(rs.getString("status"))
					.capacity(rs.getString("capacity"))
					.auditDetails(CommunityHallBookingUtil.getAuditDetails(rs)).build();

			bookingSlotDetails.add(slotDetail);
		}
		return bookingSlotDetails;
	}

}
