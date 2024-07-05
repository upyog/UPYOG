package org.upyog.chb.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.BookingSlotDetail;

@Component
public class BookingSlotDetailRowmapper implements ResultSetExtractor<List<BookingSlotDetail>> {

	@Override
	public List<BookingSlotDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<BookingSlotDetail> bookingSlotDetails = new ArrayList<>();
		while (rs.next()) {
			

			BookingSlotDetail slotDetail = BookingSlotDetail.builder().slotId(rs.getString("slot_id"))
					.bookingId(rs.getString("booking_id")).hallCode(rs.getString("hall_code"))
					.bookingDate(rs.getString("booking_date"))
					.bookingFromTime(rs.getString("booking_from_time")).bookingToTime(rs.getString("booking_to_time"))
					.status(rs.getString("status")).auditDetails(CommunityHallBookingUtil.getAuditDetails(rs)).build();

			bookingSlotDetails.add(slotDetail);
		}
		return bookingSlotDetails;
	}

}
