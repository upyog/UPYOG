package org.upyog.adv.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.adv.web.models.BookingPaymentTimerDetails;

@SuppressWarnings("java:S2638")
@Component
public class BookingPaymentTimerRowMapper implements ResultSetExtractor<List<BookingPaymentTimerDetails>> {

	@Override
	public List<BookingPaymentTimerDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {
		var timerDetails = new ArrayList<BookingPaymentTimerDetails>();
		while (rs.next()) {
			var sqlDate = rs.getDate("booking_date");
			timerDetails.add(BookingPaymentTimerDetails.builder().bookingId(rs.getString("booking_id"))
					.createdBy(rs.getString("createdby")).createdTime(rs.getLong("createdtime"))
					.status(rs.getString("status")).tenantId(rs.getString("tenant_id"))
					.addType(rs.getString("add_type")).location(rs.getString("location"))
					.faceArea(rs.getString("face_area")).nightLight((Boolean) rs.getObject("night_light"))
					.bookingDate(sqlDate != null ? sqlDate.toLocalDate() : null).build());
		}
		return timerDetails;
	}
}
