package org.upyog.chb.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityDetail;

/**
 * This class is responsible for mapping the result set from the database to a list of
 * CommunityHallSlotAvailabilityDetail objects.
 * 
 * Purpose:
 * - To extract data from the ResultSet and populate CommunityHallSlotAvailabilityDetail objects.
 * - To handle the mapping of database fields to the corresponding fields in the CommunityHallSlotAvailabilityDetail model.
 * 
 * Features:
 * - Implements the ResultSetExtractor interface to process the ResultSet.
 * - Iterates through the ResultSet and maps each row to a CommunityHallSlotAvailabilityDetail object.
 * - Uses utility methods from CommunityHallBookingUtil for date conversion and formatting.
 * 
 * Dependencies:
 * - CommunityHallBookingUtil: Provides utility methods for parsing and formatting dates.
 * - CommunityHallBookingConstants: Contains constants such as date format for consistent processing.
 * - CommunityHallSlotAvailabilityDetail: The model class representing slot availability details.
 * 
 * Fields Mapped:
 * - tenant_id: Maps to the tenantId field in CommunityHallSlotAvailabilityDetail.
 * - community_hall_code: Maps to the communityHallCode field in CommunityHallSlotAvailabilityDetail.
 * - hall_code: Maps to the hallCode field in CommunityHallSlotAvailabilityDetail.
 * - status: Maps to the slotStatus field in CommunityHallSlotAvailabilityDetail.
 * - booking_date: Maps to the bookingDate field in CommunityHallSlotAvailabilityDetail after date conversion.
 * 
 * Usage:
 * - This class is used by the repository layer to map database query results to CommunityHallSlotAvailabilityDetail objects.
 * - It ensures consistency and reusability of mapping logic across the application.
 */
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
					.bookingDate(CommunityHallBookingUtil.convertDateFormat(rs.getString("booking_date"), CommunityHallBookingConstants.DATE_FORMAT))
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
