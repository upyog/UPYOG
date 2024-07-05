package org.upyog.chb.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.chb.web.models.CommunityHallSlotAvailabiltityDetail;

@Component
public class CommunityHallSlotAvailabilityRowMapper implements ResultSetExtractor<List<CommunityHallSlotAvailabiltityDetail>> {

	@Override
	public List<CommunityHallSlotAvailabiltityDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<CommunityHallSlotAvailabiltityDetail> availabiltityDetails = new ArrayList<>();
		while (rs.next()) {
			/**
			 * chbd.tenant_id, chbd.community_hall_code, bsd.hall_code, bsd.status,bsd.booking_date
			 */
			CommunityHallSlotAvailabiltityDetail availabiltityDetail = CommunityHallSlotAvailabiltityDetail.builder()
					.bookingDate(rs.getString("booking_date"))
					.communityHallName(rs.getString("community_hall_code"))
					.hallCode(rs.getString("hall_code"))
					.slotStaus(rs.getString("status"))
					.tenantId(rs.getString("tenant_id"))
					.build();
			availabiltityDetails.add(availabiltityDetail);
		}
		return availabiltityDetails;
	}

}
