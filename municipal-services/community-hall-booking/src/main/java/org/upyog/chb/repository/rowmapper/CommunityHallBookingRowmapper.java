package org.upyog.chb.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.chb.web.models.AuditDetails;
import org.upyog.chb.web.models.BookingPurpose;
import org.upyog.chb.web.models.BookingSlotDetail;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.ResidentType;
import org.upyog.chb.web.models.SpecialCategory;

@Component
public class CommunityHallBookingRowmapper implements ResultSetExtractor<List<CommunityHallBookingDetail>> {

	@Override
	public List<CommunityHallBookingDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, CommunityHallBookingDetail> bookingDetailMap = new LinkedHashMap<>();
		List<CommunityHallBookingDetail> bookingDetails = new ArrayList<CommunityHallBookingDetail>();
		while (rs.next()) {
			String bookingId = rs.getString("booking_id");
			String bookingNo = rs.getString("booking_no");
			String tenantId = rs.getString("tenant_id");
			CommunityHallBookingDetail currentBooking = bookingDetailMap.get(bookingId);

			if (currentBooking == null) {
				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("booking_created_by"))
						.createdTime(rs.getLong("booking_created_time"))
						.lastModifiedBy(rs.getString("booking_last_modified_by"))
						.lastModifiedTime(rs.getLong("booking_last_modified_time")).build();

				ResidentType residentType = ResidentType.builder().type(rs.getString("resident_type")).build();

				SpecialCategory specialCategory = SpecialCategory.builder().category(rs.getString("special_category"))
						.build();

				BookingPurpose bookingPurpose = BookingPurpose.builder().purpose(rs.getString("purpose")).build();
				
				currentBooking = CommunityHallBookingDetail.builder().bookingId(bookingId).bookingNo(bookingNo)
						.bookingDate(rs.getLong("booking_date")).applicationNo(rs.getString("application_no"))
						.applicationDate(rs.getLong("application_date")).tenantId(tenantId)
						.applicantName(rs.getString("applicant_name"))
						.applicantMobileNo(rs.getString("applicant_mobile_no"))
						.applicantAlternateMobileNo(rs.getString("applicant_alternate_mobile_no"))
						.applicantEmailId(rs.getString("applicant_email_id"))
						.communityHallId(rs.getInt("community_hall_id"))
						.communityHallName(rs.getString("community_hall_name"))
						.bookingStatus(rs.getString("booking_status")).residentType(residentType)
						.specialCategory(specialCategory).purpose(bookingPurpose)
						.purposeDescription(rs.getString("purpose_description")).eventName(rs.getString("event_name"))
						.eventOrganisedBy(rs.getString("event_organized_by")).auditDetails(auditdetails).build();

				bookingDetailMap.put(bookingId, currentBooking);
			} else {
				currentBooking = bookingDetailMap.get(bookingId);
			}

			if (bookingDetailMap.values().size() == 0) {
				return bookingDetails;
			}

			BookingSlotDetail slotDetail = prepareSlotDetail(rs);
			currentBooking.addBookingSlots(slotDetail);

		}
		bookingDetails.addAll(bookingDetailMap.values());
		return bookingDetails;

	}

	private BookingSlotDetail prepareSlotDetail(ResultSet rs) throws SQLException {

		AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("slot_created_by"))
				.createdTime(rs.getLong("slot_created_time")).lastModifiedBy(rs.getString("slot_last_modified_by"))
				.lastModifiedTime(rs.getLong("slot_last_modified_time")).build();

		BookingSlotDetail slotDetail = BookingSlotDetail.builder().slotId(rs.getString("slot_id"))
				.bookingId(rs.getString("slot_booking_id")).hallCode(rs.getString("hall_code"))
				.hallName(rs.getString("hall_name")).bookingDate(rs.getString("booking_date"))
				.bookingFromTime(rs.getString("booking_from_time")).bookingToTime(rs.getString("booking_to_time"))
				.status(rs.getString("slot_status")).auditDetails(auditdetails).build();

		return slotDetail;
	}

}
