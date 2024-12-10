package org.upyog.adv.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;

@Component
public class AdvertisementSlotAvailabilityRowMapper implements ResultSetExtractor<List<AdvertisementSlotAvailabilityDetail>> {

	@Override
	public List<AdvertisementSlotAvailabilityDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<AdvertisementSlotAvailabilityDetail> availabiltityDetails = new ArrayList<>();
		while (rs.next()) {
		
			AdvertisementSlotAvailabilityDetail availabiltityDetail = AdvertisementSlotAvailabilityDetail.builder()
					.bookingDate(rs.getString("booking_date"))
					.addType(rs.getString("add_type"))
					.faceArea(rs.getString("face_area"))
					.location(rs.getString("location"))
					.nightLight(rs.getBoolean("night_light")) 
					.slotStaus(rs.getString("status"))
					.tenantId(rs.getString("tenant_id"))
					.build();
			availabiltityDetails.add(availabiltityDetail);
		}
		return availabiltityDetails;
	}

}
