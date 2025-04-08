package org.upyog.adv.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;

/**
 * This class implements the ResultSetExtractor interface to map rows from a database ResultSet
 * to a list of AdvertisementSlotAvailabilityDetail objects. It focuses on extracting updated slot
 * availability details for advertisements and populating them into the corresponding objects.
 */
@Component
public class AdvertisementUpdateSlotAvailabilityRowMapper implements ResultSetExtractor<List<AdvertisementSlotAvailabilityDetail>> {

	@Override
	public List<AdvertisementSlotAvailabilityDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<AdvertisementSlotAvailabilityDetail> availabiltityDetails = new ArrayList<>();
		while (rs.next()) {
		
			AdvertisementSlotAvailabilityDetail availabiltityDetail = AdvertisementSlotAvailabilityDetail.builder()
					.bookingId(rs.getString("booking_id"))
					.addType(rs.getString("add_type"))
					.location(rs.getString("location"))
					.faceArea(rs.getString("face_area"))
					.nightLight(rs.getBoolean("night_light")) 
					.bookingDate(rs.getString("booking_date"))
					.bookingStartDate(rs.getString("booking_start_date"))
					.bookingEndDate(rs.getString("booking_end_date"))
					.uuid(rs.getString("createdby"))
					.build();
			availabiltityDetails.add(availabiltityDetail);
		}
		return availabiltityDetails;
	}

}