package org.upyog.chb.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityDetail;

@Component
public class CommunityHallSlotAvailabilityRowMapper implements ResultSetExtractor<List<CommunityHallSlotAvailabilityDetail>> {

	@Override
	public List<CommunityHallSlotAvailabilityDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<CommunityHallSlotAvailabilityDetail> availabiltityDetails = new ArrayList<>();
		while (rs.next()) {
			/**
			 * chbd.tenant_id, chbd.community_hall_code, bsd.hall_code, bsd.status,bsd.booking_date
			 */
			CommunityHallSlotAvailabilityDetail availabiltityDetail = CommunityHallSlotAvailabilityDetail.builder()
					.bookingDate(rs.getString("booking_date"))
					.bookingToDate(rs.getString("booking_to_date"))
					.communityHallCode(rs.getString("community_hall_code"))
					.hallCode(rs.getString("hall_code"))
					.slotStaus(rs.getString("status"))
					.tenantId(rs.getString("tenant_id"))
					.build();
			availabiltityDetails.add(availabiltityDetail);
		}
		return availabiltityDetails;
	}

}
