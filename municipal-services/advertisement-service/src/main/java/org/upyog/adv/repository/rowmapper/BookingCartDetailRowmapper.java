package org.upyog.adv.repository.rowmapper;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.web.models.CartDetail;

@Component
public class BookingCartDetailRowmapper implements ResultSetExtractor<List<CartDetail>> {

	@Override
	public List<CartDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<CartDetail> cartDetails = new ArrayList<>();
		while (rs.next()) {
			

			CartDetail slotDetail = CartDetail.builder().cartId(rs.getString("cart_id"))
					.bookingId(rs.getString("booking_id"))
					.addType(rs.getString("add_type"))
					.bookingDate(BookingUtil.parseStringToLocalDate(rs.getString("booking_date")))
					.bookingFromTime(LocalTime.parse(rs.getString("booking_from_time")))
					.bookingToTime(LocalTime.parse(rs.getString("booking_to_time")))
					.status(rs.getString("status"))
					.faceArea(rs.getString("face_area"))
					.location(rs.getString("location"))
					//TODO; Need to parse into boolean
					.nightLight(rs.getObject("night_light") != null ? rs.getBoolean("night_light") : null) 
					.auditDetails(BookingUtil.getAuditDetails(rs))
					.build();

			cartDetails.add(slotDetail);
		}
		return cartDetails;
	}

}